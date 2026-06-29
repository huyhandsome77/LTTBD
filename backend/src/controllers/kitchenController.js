const { OrderItem, Product, Order, RestaurantTable, Category } = require('../models');
const { Op } = require('sequelize');

exports.getKitchenItems = async (req, res) => {
    try {
        const items = await OrderItem.findAll({
            where: {
                status: {
                    [Op.in]: ['WAITING', 'COOKING']
                }
            },
            include: [
                {
                    model: Product
                },
                {
                    model: Order,
                    where: {
                        status: {
                            [Op.ne]: 'CANCELLED'
                        }
                    },
                    include: [
                        {
                            model: RestaurantTable
                        }
                    ]
                }
            ],
            order: [['id', 'ASC']]
        });
        res.status(200).json(items);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

exports.updateKitchenItemStatus = async (req, res) => {
    try {
        const { id } = req.params;
        const { status } = req.body;

        if (!['COOKING', 'DONE'].includes(status)) {
            return res.status(400).json({ message: 'Invalid status for kitchen update. Must be COOKING or DONE.' });
        }

        const item = await OrderItem.findByPk(id, {
            include: [{ model: Order }]
        });

        if (!item) {
            return res.status(404).json({ message: 'OrderItem not found' });
        }

        item.status = status;
        await item.save();

        const order = item.Order;
        if (order) {
            // If item status is COOKING, and order is PENDING or CONFIRMED, transition order to PREPARING
            if (status === 'COOKING' && ['PENDING', 'CONFIRMED'].includes(order.status)) {
                order.status = 'PREPARING';
                await order.save();
            }

            // If item status is DONE, check if all sibling items are DONE or CANCELLED.
            // If all items are completed, transition order to READY (to be served).
            if (status === 'DONE') {
                const siblingItems = await OrderItem.findAll({
                    where: { order_id: order.id }
                });

                const allDone = siblingItems.every(sib => sib.status === 'DONE' || sib.status === 'CANCELLED');
                if (allDone) {
                    order.status = 'READY';
                    await order.save();
                }
            }
        }

        res.status(200).json({
            message: 'OrderItem status updated successfully',
            item
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

exports.createTestKitchenItem = async (req, res) => {
    try {
        const { tableName, productName, quantity } = req.body;

        if (!tableName || !productName || !quantity) {
            return res.status(400).json({ message: 'Vui lòng nhập đầy đủ thông tin: Tên bàn, Tên món ăn, Số lượng' });
        }

        const qty = parseInt(quantity);
        if (isNaN(qty) || qty <= 0) {
            return res.status(400).json({ message: 'Số lượng phải là số lớn hơn 0' });
        }

        // 1. Find or create RestaurantTable
        const match = tableName.match(/\d+/);
        const tableNumber = match ? parseInt(match[0]) : 99; // Default to 99 if no number found

        let [table] = await RestaurantTable.findOrCreate({
            where: { tableNumber },
            defaults: {
                qrCode: `test_qr_code_${tableNumber}`,
                status: 'AVAILABLE'
            }
        });

        // 2. Find or create a default Category
        let [category] = await Category.findOrCreate({
            where: { name: 'Món ăn thử nghiệm' },
            defaults: {
                description: 'Danh mục chứa các món ăn thử nghiệm từ Admin Console'
            }
        });

        // 3. Find or create Product
        let [product] = await Product.findOrCreate({
            where: { name: productName },
            defaults: {
                price: 50000, // Default price 50k
                isAvailable: true,
                category_id: category.id
            }
        });

        // 3. Find or create active Order
        let order = await Order.findOne({
            where: {
                table_id: table.id,
                status: {
                    [Op.in]: ['PENDING', 'CONFIRMED', 'PREPARING']
                }
            }
        });

        if (!order) {
            order = await Order.create({
                table_id: table.id,
                totalPrice: 0,
                finalPrice: 0,
                status: 'PENDING'
            });
        }

        // 4. Create OrderItem
        const unitPrice = product.price;
        const totalPrice = unitPrice * qty;
        const orderItem = await OrderItem.create({
            order_id: order.id,
            product_id: product.id,
            quantity: qty,
            unitPrice,
            totalPrice,
            status: 'WAITING'
        });

        // Update order totals
        order.totalPrice = parseFloat(order.totalPrice) + totalPrice;
        order.finalPrice = parseFloat(order.finalPrice) + totalPrice;
        await order.save();

        res.status(201).json({
            message: 'Thêm món vào bếp thành công!',
            item: orderItem
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

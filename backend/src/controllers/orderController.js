const { Order, OrderItem, Product, RestaurantTable, sequelize } = require('../models');

/**
 * Lấy hóa đơn hiện tại của một bàn
 */
exports.getCurrentOrderByTable = async (req, res, next) => {
    try {
        const { tableId } = req.params;

        const order = await Order.findOne({
            where: {
                table_id: tableId,
                paymentStatus: 'UNPAID',
                status: ['CONFIRMED', 'PREPARING', 'READY']
            },
            include: [
                {
                    model: OrderItem,
                    include: [{ model: Product }]
                }
            ]
        });

        if (!order) {
            return res.status(404).json({ message: "Bàn hiện không có hóa đơn chưa thanh toán" });
        }

        res.json(order);
    } catch (error) {
        next(error);
    }
};

/**
 * Thanh toán hóa đơn
 */
exports.payOrder = async (req, res, next) => {
    const t = await sequelize.transaction();
    try {
        const { id } = req.params;
        const order = await Order.findByPk(id);

        if (!order) {
            return res.status(404).json({ message: "Không tìm thấy đơn hàng" });
        }

        if (order.paymentStatus === 'PAID') {
            return res.status(400).json({ message: "Đơn hàng này đã được thanh toán trước đó" });
        }

        // Cập nhật trạng thái đơn hàng
        await order.update({
            paymentStatus: 'PAID',
            status: 'COMPLETED'
        }, { transaction: t });

        // Cập nhật trạng thái bàn về CLEANING (đang dọn dẹp) hoặc AVAILABLE
        if (order.table_id) {
            await RestaurantTable.update(
                { status: 'CLEANING' },
                { where: { id: order.table_id }, transaction: t }
            );
        }

        await t.commit();
        res.json({ message: "Thanh toán thành công. Bàn hiện đang được dọn dẹp." });
    } catch (error) {
        await t.rollback();
        next(error);
    }
};

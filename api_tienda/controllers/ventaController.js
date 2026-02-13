const Venta = require('../models/ventaModel');
const Admin = require('../models/adminModel');

const ventaController = {
    async getAll(req, res) {
        try {
            const ventas = await Venta.getAll();
            res.json(ventas);
        } catch (error) {
            res.status(500).json({ message: 'Error al obtener ventas', error: error.message });
        }
    },

    async getById(req, res) {
        try {
            const venta = await Venta.getById(req.params.id);
            if (!venta) {
                return res.status(404).json({ message: 'Venta no encontrada' });
            }
            res.json(venta);
        } catch (error) {
            res.status(500).json({ message: 'Error al obtener venta', error: error.message });
        }
    },

    async create(req, res) {
        try {
            const { id_usuario, total, metodo_pago, productos } = req.body;
            
            const id_venta = await Venta.create({
                id_usuario,
                total,
                metodo_pago,
                productos
            });
            
            res.status(201).json({
                message: 'Venta registrada exitosamente',
                id_venta
            });
        } catch (error) {
            res.status(500).json({ message: 'Error al registrar venta', error: error.message });
        }
    },

    async getByUsuario(req, res) {
        try {
            const ventas = await Venta.getByUsuario(req.params.id_usuario);
            res.json(ventas);
        } catch (error) {
            res.status(500).json({ message: 'Error al obtener ventas del usuario', error: error.message });
        }
    }
};

module.exports = ventaController;
const Carrito = require('../models/carritoModel');

const carritoController = {
    async getByUsuario(req, res) {
        try {
            const items = await Carrito.getByUsuario(req.params.id_usuario);
            res.json(items);
        } catch (error) {
            res.status(500).json({ message: 'Error al obtener carrito', error: error.message });
        }
    },

    async addItem(req, res) {
        try {
            const { id_usuario, id_producto, cantidad } = req.body;
            
            await Carrito.addItem(id_usuario, id_producto, cantidad);
            
            const items = await Carrito.getByUsuario(id_usuario);
            res.json({
                message: 'Producto agregado al carrito',
                carrito: items
            });
        } catch (error) {
            res.status(500).json({ message: 'Error al agregar al carrito', error: error.message });
        }
    },

    async updateCantidad(req, res) {
        try {
            const { id_usuario, id_producto, cantidad } = req.body;
            
            await Carrito.updateCantidad(id_usuario, id_producto, cantidad);
            
            const items = await Carrito.getByUsuario(id_usuario);
            res.json({
                message: 'Cantidad actualizada',
                carrito: items
            });
        } catch (error) {
            res.status(500).json({ message: 'Error al actualizar cantidad', error: error.message });
        }
    },

    async removeItem(req, res) {
        try {
            const { id_usuario, id_producto } = req.params;
            
            await Carrito.removeItem(id_usuario, id_producto);
            
            const items = await Carrito.getByUsuario(id_usuario);
            res.json({
                message: 'Producto eliminado del carrito',
                carrito: items
            });
        } catch (error) {
            res.status(500).json({ message: 'Error al eliminar del carrito', error: error.message });
        }
    },

    async clear(req, res) {
        try {
            const { id_usuario } = req.params;
            
            await Carrito.clear(id_usuario);
            res.json({ message: 'Carrito vaciado exitosamente' });
        } catch (error) {
            res.status(500).json({ message: 'Error al vaciar carrito', error: error.message });
        }
    }
};

module.exports = carritoController;
const Producto = require('../models/productoModel');
const Admin = require('../models/adminModel');

const productoController = {
    async getAll(req, res) {
        try {
            const productos = await Producto.getAll();
            res.json(productos);
        } catch (error) {
            res.status(500).json({ message: 'Error al obtener productos', error: error.message });
        }
    },

    async getById(req, res) {
        try {
            const producto = await Producto.getById(req.params.id);
            if (!producto) {
                return res.status(404).json({ message: 'Producto no encontrado' });
            }
            res.json(producto);
        } catch (error) {
            res.status(500).json({ message: 'Error al obtener producto', error: error.message });
        }
    },

    async create(req, res) {
        try {
            const { nombre, descripcion, precio, stock, id_categoria, imagen_url } = req.body;
            
            const id_producto = await Producto.create({
                nombre,
                descripcion,
                precio,
                stock,
                id_categoria,
                imagen_url
            });
            
            // Registrar acción en historial
            await Admin.registerAction(
                req.userId,
                'Crear producto',
                `Producto "${nombre}" creado con ID ${id_producto}`
            );
            
            res.status(201).json({
                message: 'Producto creado exitosamente',
                id_producto
            });
        } catch (error) {
            res.status(500).json({ message: 'Error al crear producto', error: error.message });
        }
    },

    async update(req, res) {
        try {
            const { id } = req.params;
            const { nombre, descripcion, precio, stock, id_categoria, imagen_url } = req.body;
            
            const affectedRows = await Producto.update(id, {
                nombre,
                descripcion,
                precio,
                stock,
                id_categoria,
                imagen_url
            });
            
            if (affectedRows === 0) {
                return res.status(404).json({ message: 'Producto no encontrado' });
            }
            
            await Admin.registerAction(
                req.userId,
                'Actualizar producto',
                `Producto "${nombre}" actualizado`
            );
            
            res.json({ message: 'Producto actualizado exitosamente' });
        } catch (error) {
            res.status(500).json({ message: 'Error al actualizar producto', error: error.message });
        }
    },

    async disable(req, res) {
        try {
            const { id } = req.params;
            const producto = await Producto.getById(id);
            
            if (!producto) {
                return res.status(404).json({ message: 'Producto no encontrado' });
            }
            
            await Producto.disable(id);
            
            await Admin.registerAction(
                req.userId,
                'Deshabilitar producto',
                `Producto "${producto.nombre}" deshabilitado temporalmente`
            );
            
            res.json({ message: 'Producto deshabilitado exitosamente' });
        } catch (error) {
            res.status(500).json({ message: 'Error al deshabilitar producto', error: error.message });
        }
    },

    async enable(req, res) {
        try {
            const { id } = req.params;
            const producto = await Producto.getById(id);
            
            if (!producto) {
                return res.status(404).json({ message: 'Producto no encontrado' });
            }
            
            await Producto.enable(id);
            
            await Admin.registerAction(
                req.userId,
                'Habilitar producto',
                `Producto "${producto.nombre}" habilitado nuevamente`
            );
            
            res.json({ message: 'Producto habilitado exitosamente' });
        } catch (error) {
            res.status(500).json({ message: 'Error al habilitar producto', error: error.message });
        }
    },

    async getCategorias(req, res) {
        try {
            const categorias = await Producto.getCategorias();
            res.json(categorias);
        } catch (error) {
            res.status(500).json({ message: 'Error al obtener categorías', error: error.message });
        }
    }
};

module.exports = productoController;
const db = require('../config/database');

class Producto {
    static async getAll() {
        const [rows] = await db.execute(`
            SELECT p.*, c.nombre as categoria_nombre 
            FROM productos p 
            LEFT JOIN categorias c ON p.id_categoria = c.id_categoria
            WHERE p.estado = true
            ORDER BY p.fecha_creacion DESC
        `);
        return rows;
    }

    static async getById(id) {
        const [rows] = await db.execute(
            'SELECT * FROM productos WHERE id_producto = ?',
            [id]
        );
        return rows[0];
    }

    static async create(productoData) {
        const { nombre, descripcion, precio, stock, id_categoria, imagen_url } = productoData;
        const [result] = await db.execute(
            'INSERT INTO productos (nombre, descripcion, precio, stock, id_categoria, imagen_url) VALUES (?, ?, ?, ?, ?, ?)',
            [nombre, descripcion, precio, stock, id_categoria, imagen_url]
        );
        return result.insertId;
    }

    static async update(id, productoData) {
        const { nombre, descripcion, precio, stock, id_categoria, imagen_url } = productoData;
        const [result] = await db.execute(
            'UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ?, id_categoria = ?, imagen_url = ? WHERE id_producto = ?',
            [nombre, descripcion, precio, stock, id_categoria, imagen_url, id]
        );
        return result.affectedRows;
    }

    static async disable(id) {
        const [result] = await db.execute(
            'UPDATE productos SET estado = false WHERE id_producto = ?',
            [id]
        );
        return result.affectedRows;
    }

    static async enable(id) {
        const [result] = await db.execute(
            'UPDATE productos SET estado = true WHERE id_producto = ?',
            [id]
        );
        return result.affectedRows;
    }

    static async getCategorias() {
        const [rows] = await db.execute('SELECT * FROM categorias ORDER BY nombre');
        return rows;
    }
}

module.exports = Producto;
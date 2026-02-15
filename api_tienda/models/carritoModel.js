const db = require('../config/database');

class Carrito {
    static async getByUsuario(id_usuario) {
        const [rows] = await db.execute(`
            SELECT c.*, p.nombre, p.precio, p.imagen_url 
            FROM carrito c 
            JOIN productos p ON c.id_producto = p.id_producto 
            WHERE c.id_usuario = ?
        `, [id_usuario]);
        return rows;
    }

    static async addItem(id_usuario, id_producto, cantidad) {
        // Verificar si el producto ya está en el carrito
        const [existing] = await db.execute(
            'SELECT * FROM carrito WHERE id_usuario = ? AND id_producto = ?',
            [id_usuario, id_producto]
        );
        
        if (existing.length > 0) {
            // Actualizar cantidad
            const [result] = await db.execute(
                'UPDATE carrito SET cantidad = cantidad + ? WHERE id_usuario = ? AND id_producto = ?',
                [cantidad, id_usuario, id_producto]
            );
            return result.affectedRows;
        } else {
            // Insertar nuevo item
            const [result] = await db.execute(
                'INSERT INTO carrito (id_usuario, id_producto, cantidad) VALUES (?, ?, ?)',
                [id_usuario, id_producto, cantidad]
            );
            return result.insertId;
        }
    }

    static async updateCantidad(id_usuario, id_producto, cantidad) {
        const [result] = await db.execute(
            'UPDATE carrito SET cantidad = ? WHERE id_usuario = ? AND id_producto = ?',
            [cantidad, id_usuario, id_producto]
        );
        return result.affectedRows;
    }

    static async removeItem(id_usuario, id_producto) {
        const [result] = await db.execute(
            'DELETE FROM carrito WHERE id_usuario = ? AND id_producto = ?',
            [id_usuario, id_producto]
        );
        return result.affectedRows;
    }

    static async clear(id_usuario) {
        const [result] = await db.execute(
            'DELETE FROM carrito WHERE id_usuario = ?',
            [id_usuario]
        );
        return result.affectedRows;
    }
}

module.exports = Carrito;
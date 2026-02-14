const db = require('../config/database');

class Venta {
    static async getAll() {
        const [rows] = await db.execute(`
            SELECT v.*, u.nombre as usuario_nombre 
            FROM ventas v 
            JOIN usuarios u ON v.id_usuario = u.id_usuario 
            ORDER BY v.fecha_venta DESC
        `);
        return rows;
    }

    static async getById(id) {
        const [venta] = await db.execute(
            'SELECT v.*, u.nombre as usuario_nombre FROM ventas v JOIN usuarios u ON v.id_usuario = u.id_usuario WHERE v.id_venta = ?',
            [id]
        );
        
        if (venta.length > 0) {
            const [detalles] = await db.execute(`
                SELECT dv.*, p.nombre as producto_nombre 
                FROM detalles_venta dv 
                JOIN productos p ON dv.id_producto = p.id_producto 
                WHERE dv.id_venta = ?
            `, [id]);
            
            venta[0].detalles = detalles;
        }
        
        return venta[0];
    }

    static async create(ventaData) {
        const { id_usuario, total, metodo_pago, productos } = ventaData;
        
        const connection = await db.getConnection();
        try {
            await connection.beginTransaction();
            
            // Crear la venta
            const [result] = await connection.execute(
                'INSERT INTO ventas (id_usuario, total, metodo_pago) VALUES (?, ?, ?)',
                [id_usuario, total, metodo_pago]
            );
            
            const id_venta = result.insertId;
            
            // Insertar detalles de la venta
            for (const item of productos) {
                await connection.execute(
                    'INSERT INTO detalles_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)',
                    [id_venta, item.id_producto, item.cantidad, item.precio_unitario, item.subtotal]
                );
                
                // Actualizar stock
                await connection.execute(
                    'UPDATE productos SET stock = stock - ? WHERE id_producto = ?',
                    [item.cantidad, item.id_producto]
                );
            }
            
            // Limpiar carrito del usuario
            await connection.execute(
                'DELETE FROM carrito WHERE id_usuario = ?',
                [id_usuario]
            );
            
            await connection.commit();
            return id_venta;
            
        } catch (error) {
            await connection.rollback();
            throw error;
        } finally {
            connection.release();
        }
    }

    static async getByUsuario(id_usuario) {
        const [rows] = await db.execute(
            'SELECT * FROM ventas WHERE id_usuario = ? ORDER BY fecha_venta DESC',
            [id_usuario]
        );
        return rows;
    }
}

module.exports = Venta;
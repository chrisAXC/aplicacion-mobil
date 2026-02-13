const db = require('../config/database');

class Usuario {
    // Obtener todos los usuarios activos
    static async getAll() {
        const [rows] = await db.execute(`
            SELECT id_usuario, nombre, email, telefono, direccion, 
                   fecha_registro, estado 
            FROM usuarios 
            WHERE estado = true
            ORDER BY fecha_registro DESC
        `);
        return rows;
    }

    // Obtener usuario por ID
    static async getById(id) {
        const [rows] = await db.execute(`
            SELECT id_usuario, nombre, email, telefono, direccion, 
                   fecha_registro, estado 
            FROM usuarios 
            WHERE id_usuario = ? AND estado = true
        `, [id]);
        return rows[0];
    }

    // Buscar usuario por email (incluye contraseña para autenticación)
    static async findByEmail(email) {
        const [rows] = await db.execute(
            'SELECT * FROM usuarios WHERE email = ? AND estado = true',
            [email]
        );
        return rows[0];
    }

    // Crear nuevo usuario
    static async create(usuarioData) {
        const { nombre, email, password, telefono, direccion } = usuarioData;
        const [result] = await db.execute(
            `INSERT INTO usuarios 
             (nombre, email, password, telefono, direccion) 
             VALUES (?, ?, ?, ?, ?)`,
            [nombre, email, password, telefono || null, direccion || null]
        );
        return result.insertId;
    }

    // Actualizar usuario
    static async update(id, usuarioData) {
        const { nombre, telefono, direccion } = usuarioData;
        const [result] = await db.execute(
            `UPDATE usuarios 
             SET nombre = ?, telefono = ?, direccion = ? 
             WHERE id_usuario = ? AND estado = true`,
            [nombre, telefono, direccion, id]
        );
        return result.affectedRows;
    }

    // Buscar usuarios (búsqueda avanzada)
    static async search(termino) {
        const [rows] = await db.execute(`
            SELECT id_usuario, nombre, email, telefono, direccion, fecha_registro
            FROM usuarios 
            WHERE estado = true 
            AND (nombre LIKE ? OR email LIKE ? OR telefono LIKE ?)
            ORDER BY nombre
        `, [`%${termino}%`, `%${termino}%`, `%${termino}%`]);
        return rows;
    }

    // Obtener usuarios inactivos (solo admin)
    static async getInactivos() {
        const [rows] = await db.execute(`
            SELECT id_usuario, nombre, email, telefono, direccion, fecha_registro
            FROM usuarios 
            WHERE estado = false
            ORDER BY fecha_registro DESC
        `);
        return rows;
    }

    // Verificar si el email existe
    static async emailExists(email, excludeId = null) {
        let query = 'SELECT COUNT(*) as count FROM usuarios WHERE email = ?';
        const params = [email];
        
        if (excludeId) {
            query += ' AND id_usuario != ?';
            params.push(excludeId);
        }
        
        const [rows] = await db.execute(query, params);
        return rows[0].count > 0;
    }

    // Contar usuarios totales
    static async count() {
        const [rows] = await db.execute(
            'SELECT COUNT(*) as total FROM usuarios WHERE estado = true'
        );
        return rows[0].total;
    }

    // Obtener usuarios registrados por mes (estadísticas)
    static async getRegistrosPorMes() {
        const [rows] = await db.execute(`
            SELECT 
                YEAR(fecha_registro) as año,
                MONTH(fecha_registro) as mes,
                COUNT(*) as total
            FROM usuarios
            WHERE YEAR(fecha_registro) = YEAR(CURRENT_DATE())
            GROUP BY YEAR(fecha_registro), MONTH(fecha_registro)
            ORDER BY mes ASC
        `);
        return rows;
    }
}

module.exports = Usuario;
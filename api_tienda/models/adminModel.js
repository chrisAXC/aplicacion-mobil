const db = require('../config/database');

class Admin {
    static async findByEmail(email) {
        const [rows] = await db.execute(
            'SELECT * FROM administradores WHERE email = ? AND estado = true',
            [email]
        );
        return rows[0];
    }

    static async create(adminData) {
        const { nombre, email, password } = adminData;
        const [result] = await db.execute(
            'INSERT INTO administradores (nombre, email, password) VALUES (?, ?, ?)',
            [nombre, email, password]
        );
        return result.insertId;
    }

    static async registerAction(id_admin, accion, descripcion) {
        const [result] = await db.execute(
            'INSERT INTO historial_cambios (id_admin, accion, descripcion) VALUES (?, ?, ?)',
            [id_admin, accion, descripcion]
        );
        return result.insertId;
    }
}

module.exports = Admin;
const Usuario = require('../models/usuarioModel');
const Admin = require('../models/adminModel');
const bcrypt = require('bcryptjs');

const usuarioController = {
    
    async getAll(req, res) {
        try {
            const usuarios = await Usuario.getAll();
            res.json(usuarios);
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al obtener usuarios', 
                error: error.message 
            });
        }
    },

    // Obtener usuario por ID
    async getById(req, res) {
        try {
            const { id } = req.params;
            
            // Verificar que el usuario solo pueda ver su propio perfil
            // o que sea admin
            if (req.userTipo !== 'admin' && req.userId != id) {
                return res.status(403).json({ 
                    message: 'No tienes permiso para ver este usuario' 
                });
            }

            const usuario = await Usuario.getById(id);
            
            if (!usuario) {
                return res.status(404).json({ 
                    message: 'Usuario no encontrado' 
                });
            }

            res.json(usuario);
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al obtener usuario', 
                error: error.message 
            });
        }
    },

    // Crear usuario (registro público)
    async create(req, res) {
        try {
            const { nombre, email, password, telefono, direccion } = req.body;

            // Validar campos requeridos
            if (!nombre || !email || !password) {
                return res.status(400).json({ 
                    message: 'Nombre, email y contraseña son requeridos' 
                });
            }

            // Verificar si el email ya existe
            const existingUser = await Usuario.findByEmail(email);
            if (existingUser) {
                return res.status(400).json({ 
                    message: 'El email ya está registrado' 
                });
            }

            // Encriptar contraseña
            const hashedPassword = await bcrypt.hash(password, 10);

            const id_usuario = await Usuario.create({
                nombre,
                email,
                password: hashedPassword,
                telefono,
                direccion
            });

            res.status(201).json({
                message: 'Usuario creado exitosamente',
                id_usuario
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al crear usuario', 
                error: error.message 
            });
        }
    },

    // Actualizar usuario
    async update(req, res) {
        try {
            const { id } = req.params;
            const { nombre, telefono, direccion } = req.body;

            // Verificar que el usuario solo pueda actualizar su propio perfil
            // o que sea admin
            if (req.userTipo !== 'admin' && req.userId != id) {
                return res.status(403).json({ 
                    message: 'No tienes permiso para actualizar este usuario' 
                });
            }

            // Verificar que el usuario existe
            const usuario = await Usuario.getById(id);
            if (!usuario) {
                return res.status(404).json({ 
                    message: 'Usuario no encontrado' 
                });
            }

            const affectedRows = await Usuario.update(id, {
                nombre,
                telefono,
                direccion
            });

            if (affectedRows === 0) {
                return res.status(404).json({ 
                    message: 'No se pudo actualizar el usuario' 
                });
            }

            // Registrar acción en historial si es admin
            if (req.userTipo === 'admin') {
                await Admin.registerAction(
                    req.userId,
                    'Actualizar usuario',
                    `Usuario ID ${id} actualizado por administrador`
                );
            }

            res.json({ 
                message: 'Usuario actualizado exitosamente' 
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al actualizar usuario', 
                error: error.message 
            });
        }
    },

    // Cambiar contraseña
    async changePassword(req, res) {
        try {
            const { id } = req.params;
            const { password_actual, password_nuevo } = req.body;

            // Verificar que el usuario solo pueda cambiar su propia contraseña
            if (req.userId != id) {
                return res.status(403).json({ 
                    message: 'No tienes permiso para cambiar esta contraseña' 
                });
            }

            // Obtener usuario con contraseña
            const usuario = await Usuario.findByEmail(req.userEmail);
            
            // Verificar contraseña actual
            const isValidPassword = await bcrypt.compare(password_actual, usuario.password);
            if (!isValidPassword) {
                return res.status(401).json({ 
                    message: 'Contraseña actual incorrecta' 
                });
            }

            // Encriptar nueva contraseña
            const hashedPassword = await bcrypt.hash(password_nuevo, 10);

            // Actualizar contraseña
            const db = require('../config/database');
            await db.execute(
                'UPDATE usuarios SET password = ? WHERE id_usuario = ?',
                [hashedPassword, id]
            );

            res.json({ 
                message: 'Contraseña actualizada exitosamente' 
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al cambiar contraseña', 
                error: error.message 
            });
        }
    },

    // Deshabilitar usuario (solo admin)
    async disable(req, res) {
        try {
            const { id } = req.params;

            // Solo admin puede deshabilitar usuarios
            if (req.userTipo !== 'admin') {
                return res.status(403).json({ 
                    message: 'Acceso no autorizado' 
                });
            }

            const db = require('../config/database');
            const [result] = await db.execute(
                'UPDATE usuarios SET estado = false WHERE id_usuario = ?',
                [id]
            );

            if (result.affectedRows === 0) {
                return res.status(404).json({ 
                    message: 'Usuario no encontrado' 
                });
            }

            // Registrar acción en historial
            await Admin.registerAction(
                req.userId,
                'Deshabilitar usuario',
                `Usuario ID ${id} deshabilitado`
            );

            res.json({ 
                message: 'Usuario deshabilitado exitosamente' 
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al deshabilitar usuario', 
                error: error.message 
            });
        }
    },

    // Habilitar usuario (solo admin)
    async enable(req, res) {
        try {
            const { id } = req.params;

            if (req.userTipo !== 'admin') {
                return res.status(403).json({ 
                    message: 'Acceso no autorizado' 
                });
            }

            const db = require('../config/database');
            const [result] = await db.execute(
                'UPDATE usuarios SET estado = true WHERE id_usuario = ?',
                [id]
            );

            if (result.affectedRows === 0) {
                return res.status(404).json({ 
                    message: 'Usuario no encontrado' 
                });
            }

            await Admin.registerAction(
                req.userId,
                'Habilitar usuario',
                `Usuario ID ${id} habilitado`
            );

            res.json({ 
                message: 'Usuario habilitado exitosamente' 
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al habilitar usuario', 
                error: error.message 
            });
        }
    },

    // Obtener dirección de envío
    async getDireccion(req, res) {
        try {
            const { id } = req.params;

            if (req.userId != id && req.userTipo !== 'admin') {
                return res.status(403).json({ 
                    message: 'No tienes permiso para ver esta información' 
                });
            }

            const db = require('../config/database');
            const [rows] = await db.execute(
                'SELECT direccion FROM usuarios WHERE id_usuario = ?',
                [id]
            );

            if (rows.length === 0) {
                return res.status(404).json({ 
                    message: 'Usuario no encontrado' 
                });
            }

            res.json({ 
                direccion: rows[0].direccion 
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al obtener dirección', 
                error: error.message 
            });
        }
    },

    // Actualizar dirección de envío
    async updateDireccion(req, res) {
        try {
            const { id } = req.params;
            const { direccion } = req.body;

            if (req.userId != id) {
                return res.status(403).json({ 
                    message: 'No tienes permiso para actualizar esta información' 
                });
            }

            const db = require('../config/database');
            const [result] = await db.execute(
                'UPDATE usuarios SET direccion = ? WHERE id_usuario = ?',
                [direccion, id]
            );

            if (result.affectedRows === 0) {
                return res.status(404).json({ 
                    message: 'Usuario no encontrado' 
                });
            }

            res.json({ 
                message: 'Dirección actualizada exitosamente' 
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al actualizar dirección', 
                error: error.message 
            });
        }
    },

    // Obtener historial de compras del usuario
    async getHistorialCompras(req, res) {
        try {
            const { id } = req.params;

            if (req.userId != id && req.userTipo !== 'admin') {
                return res.status(403).json({ 
                    message: 'No tienes permiso para ver este historial' 
                });
            }

            const db = require('../config/database');
            const [ventas] = await db.execute(`
                SELECT v.*, 
                       COUNT(dv.id_detalle) as total_productos
                FROM ventas v
                LEFT JOIN detalles_venta dv ON v.id_venta = dv.id_venta
                WHERE v.id_usuario = ?
                GROUP BY v.id_venta
                ORDER BY v.fecha_venta DESC
            `, [id]);

            res.json(ventas);
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al obtener historial de compras', 
                error: error.message 
            });
        }
    },

    // Estadísticas de usuario (solo admin)
    async getEstadisticas(req, res) {
        try {
            if (req.userTipo !== 'admin') {
                return res.status(403).json({ 
                    message: 'Acceso no autorizado' 
                });
            }

            const db = require('../config/database');
            
            // Total de usuarios
            const [total] = await db.execute(
                'SELECT COUNT(*) as total FROM usuarios WHERE estado = true'
            );

            // Usuarios nuevos este mes
            const [nuevos] = await db.execute(`
                SELECT COUNT(*) as total 
                FROM usuarios 
                WHERE MONTH(fecha_registro) = MONTH(CURRENT_DATE())
                AND YEAR(fecha_registro) = YEAR(CURRENT_DATE())
            `);

            // Top compradores
            const [topCompradores] = await db.execute(`
                SELECT u.id_usuario, u.nombre, u.email,
                       COUNT(v.id_venta) as total_compras,
                       SUM(v.total) as total_gastado
                FROM usuarios u
                JOIN ventas v ON u.id_usuario = v.id_usuario
                WHERE u.estado = true
                GROUP BY u.id_usuario
                ORDER BY total_gastado DESC
                LIMIT 5
            `);

            res.json({
                total_usuarios: total[0].total,
                nuevos_este_mes: nuevos[0].total,
                top_compradores: topCompradores
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Error al obtener estadísticas', 
                error: error.message 
            });
        }
    }
};

// LOGIN de usuario (NUEVA FUNCIÓN)
async function login(req, res) {
    try {
        const { email, password } = req.body;

        // Validar campos requeridos
        if (!email || !password) {
            return res.status(400).json({ 
                message: 'Email y contraseña son requeridos' 
            });
        }

        // Buscar usuario por email (incluye password)
        const usuario = await Usuario.findByEmail(email);
        
        if (!usuario) {
            return res.status(401).json({ 
                message: 'Credenciales inválidas' 
            });
        }

        // Verificar contraseña
        const validPassword = await bcrypt.compare(password, usuario.password);
        
        if (!validPassword) {
            return res.status(401).json({ 
                message: 'Credenciales inválidas' 
            });
        }

        // Generar token JWT (si tienes JWT instalado)
        // Si no tienes JWT, podemos crear un token simple
        const jwt = require('jsonwebtoken');
        const token = jwt.sign(
            { 
                id: usuario.id_usuario, 
                email: usuario.email,
                tipo: 'usuario'
            }, 
            'tu_secreto_jwt', // Cambia esto por una variable de entorno
            { expiresIn: '7d' }
        );

        // Enviar respuesta sin contraseña
        const { password: _, ...usuarioSinPassword } = usuario;
        
        res.json({
            message: 'Login exitoso',
            token: token,
            usuario: usuarioSinPassword
        });

    } catch (error) {
        console.error('Error en login:', error);
        res.status(500).json({ 
            message: 'Error en el servidor', 
            error: error.message 
        });
    }
}

// Agrega esta línea al final, dentro de module.exports
module.exports = {
    getAll,
    getById,
    create,
    update,
    changePassword,
    disable,
    enable,
    getDireccion,
    updateDireccion,
    getHistorialCompras,
    getEstadisticas,
    login  // ← AGREGAR ESTA LÍNEA
};

module.exports = usuarioController;
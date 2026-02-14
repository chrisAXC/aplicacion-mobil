const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const Admin = require('../models/adminModel');
const Usuario = require('../models/usuarioModel');

const authController = {
    // Login de administrador
    async loginAdmin(req, res) {
        try {
            const { email, password } = req.body;
            
            const admin = await Admin.findByEmail(email);
            
            if (!admin) {
                return res.status(401).json({ message: 'Credenciales inválidas' });
            }
            
            const isValidPassword = await bcrypt.compare(password, admin.password);
            
            if (!isValidPassword) {
                return res.status(401).json({ message: 'Credenciales inválidas' });
            }
            
            const token = jwt.sign(
                { id: admin.id_admin, email: admin.email, tipo: 'admin' },
                process.env.JWT_SECRET,
                { expiresIn: '24h' }
            );
            
            res.json({
                token,
                usuario: {
                    id: admin.id_admin,
                    nombre: admin.nombre,
                    email: admin.email,
                    tipo: 'admin'
                }
            });
            
        } catch (error) {
            res.status(500).json({ message: 'Error en el servidor', error: error.message });
        }
    },

    // Registro de usuario (cliente)
    async registerUsuario(req, res) {
        try {
            const { nombre, email, password, telefono, direccion } = req.body;
            
            const existingUser = await Usuario.findByEmail(email);
            if (existingUser) {
                return res.status(400).json({ message: 'El email ya está registrado' });
            }
            
            const hashedPassword = await bcrypt.hash(password, 10);
            
            const userId = await Usuario.create({
                nombre,
                email,
                password: hashedPassword,
                telefono,
                direccion
            });
            
            const token = jwt.sign(
                { id: userId, email, tipo: 'usuario' },
                process.env.JWT_SECRET,
                { expiresIn: '24h' }
            );
            
            res.status(201).json({
                message: 'Usuario registrado exitosamente',
                token,
                usuario: {
                    id: userId,
                    nombre,
                    email,
                    telefono,
                    direccion
                }
            });
            
        } catch (error) {
            res.status(500).json({ message: 'Error en el servidor', error: error.message });
        }
    },

    // Login de usuario (cliente)
    async loginUsuario(req, res) {
        try {
            const { email, password } = req.body;
            
            const usuario = await Usuario.findByEmail(email);
            
            if (!usuario) {
                return res.status(401).json({ message: 'Credenciales inválidas' });
            }
            
            const isValidPassword = await bcrypt.compare(password, usuario.password);
            
            if (!isValidPassword) {
                return res.status(401).json({ message: 'Credenciales inválidas' });
            }
            
            const token = jwt.sign(
                { id: usuario.id_usuario, email: usuario.email, tipo: 'usuario' },
                process.env.JWT_SECRET,
                { expiresIn: '24h' }
            );
            
            res.json({
                token,
                usuario: {
                    id: usuario.id_usuario,
                    nombre: usuario.nombre,
                    email: usuario.email,
                    telefono: usuario.telefono,
                    direccion: usuario.direccion,
                    tipo: 'usuario'
                }
            });
            
        } catch (error) {
            res.status(500).json({ message: 'Error en el servidor', error: error.message });
        }
    }
};

module.exports = authController;
const usuarioController = require('../controllers/usuarioController');

const validationMiddleware = {
    // Validar registro de usuario
    validateRegister(req, res, next) {
        const { nombre, email, password } = req.body;
        const errors = [];

        if (!nombre || nombre.trim().length < 3) {
            errors.push('El nombre debe tener al menos 3 caracteres');
        }

        if (!email || !email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
            errors.push('Email inválido');
        }

        if (!password || password.length < 6) {
            errors.push('La contraseña debe tener al menos 6 caracteres');
        }

        if (req.body.telefono && !req.body.telefono.match(/^[0-9]{10}$/)) {
            errors.push('Teléfono inválido (10 dígitos)');
        }

        if (errors.length > 0) {
            return res.status(400).json({ errors });
        }

        next();
    },

    // Validar login
    validateLogin(req, res, next) {
        const { email, password } = req.body;
        const errors = [];

        if (!email) errors.push('Email requerido');
        if (!password) errors.push('Contraseña requerida');

        if (errors.length > 0) {
            return res.status(400).json({ errors });
        }

        next();
    },

    // Validar ID
    validateId(req, res, next) {
        const { id } = req.params;
        
        if (!id || isNaN(id) || id <= 0) {
            return res.status(400).json({ 
                message: 'ID inválido' 
            });
        }

        next();
    }
};

module.exports = validationMiddleware;
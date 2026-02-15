const express = require('express');
const router = express.Router();
const usuarioController = require('../controllers/usuarioController');
const authMiddleware = require('../middlewares/authMiddleware');
const validationMiddleware = require('../middlewares/validationMiddleware');

// ============= RUTAS PÚBLICAS (SIN AUTENTICACIÓN) =============
// Registro de usuario
router.post('/registro', 
    validationMiddleware.validateRegister, 
    usuarioController.create
);

// LOGIN de usuario - ¡ESTA ES LA RUTA QUE FALTABA!
router.post('/login', 
    usuarioController.login
);

// ============= RUTAS PROTEGIDAS (CON AUTENTICACIÓN) =============

// Obtener todos los usuarios (solo admin)
router.get('/', 
    authMiddleware.verifyToken, 
    authMiddleware.verifyAdmin, 
    usuarioController.getAll
);

// Estadísticas (solo admin)
router.get('/estadisticas', 
    authMiddleware.verifyToken, 
    authMiddleware.verifyAdmin, 
    usuarioController.getEstadisticas
);

// Obtener usuario por ID
router.get('/:id', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.getById
);

// Obtener historial de compras
router.get('/:id/historial', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.getHistorialCompras
);

// Obtener dirección
router.get('/:id/direccion', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.getDireccion
);

// Actualizar usuario
router.put('/:id', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.update
);

// Cambiar contraseña
router.put('/:id/password', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.changePassword
);

// Actualizar dirección
router.put('/:id/direccion', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.updateDireccion
);

// ============= RUTAS SOLO ADMIN =============

// Deshabilitar usuario
router.put('/:id/disable', 
    authMiddleware.verifyToken, 
    authMiddleware.verifyAdmin,
    validationMiddleware.validateId,
    usuarioController.disable
);

// Habilitar usuario
router.put('/:id/enable', 
    authMiddleware.verifyToken, 
    authMiddleware.verifyAdmin,
    validationMiddleware.validateId,
    usuarioController.enable
);

module.exports = router;
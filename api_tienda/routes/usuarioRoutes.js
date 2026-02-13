const express = require('express');
const router = express.Router();
const usuarioController = require('../controllers/usuarioController');
const authMiddleware = require('../middlewares/authMiddleware');
const validationMiddleware = require('../middlewares/validationMiddleware');

// Rutas públicas
router.post('/', 
    validationMiddleware.validateRegister, 
    usuarioController.create
);

// Rutas protegidas
router.get('/', 
    authMiddleware.verifyToken, 
    authMiddleware.verifyAdmin, 
    usuarioController.getAll
);

router.get('/estadisticas', 
    authMiddleware.verifyToken, 
    authMiddleware.verifyAdmin, 
    usuarioController.getEstadisticas
);

router.get('/:id', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.getById
);

router.get('/:id/historial', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.getHistorialCompras
);

router.get('/:id/direccion', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.getDireccion
);

router.put('/:id', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.update
);

router.put('/:id/password', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.changePassword
);

router.put('/:id/direccion', 
    authMiddleware.verifyToken, 
    validationMiddleware.validateId,
    usuarioController.updateDireccion
);

// Rutas solo admin
router.put('/:id/disable', 
    authMiddleware.verifyToken, 
    authMiddleware.verifyAdmin,
    validationMiddleware.validateId,
    usuarioController.disable
);

router.put('/:id/enable', 
    authMiddleware.verifyToken, 
    authMiddleware.verifyAdmin,
    validationMiddleware.validateId,
    usuarioController.enable
);

module.exports = router;
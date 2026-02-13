const express = require('express');
const router = express.Router();
const carritoController = require('../controllers/carritoController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/:id_usuario', authMiddleware.verifyToken, carritoController.getByUsuario);
router.post('/add', authMiddleware.verifyToken, carritoController.addItem);
router.put('/update', authMiddleware.verifyToken, carritoController.updateCantidad);
router.delete('/remove/:id_usuario/:id_producto', authMiddleware.verifyToken, carritoController.removeItem);
router.delete('/clear/:id_usuario', authMiddleware.verifyToken, carritoController.clear);

module.exports = router;
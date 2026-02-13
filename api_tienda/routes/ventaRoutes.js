const express = require('express');
const router = express.Router();
const ventaController = require('../controllers/ventaController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/', authMiddleware.verifyToken, authMiddleware.verifyAdmin, ventaController.getAll);
router.get('/:id', authMiddleware.verifyToken, ventaController.getById);
router.get('/usuario/:id_usuario', authMiddleware.verifyToken, ventaController.getByUsuario);
router.post('/', authMiddleware.verifyToken, ventaController.create);

module.exports = router;
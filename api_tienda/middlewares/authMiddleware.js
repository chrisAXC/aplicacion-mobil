const jwt = require('jsonwebtoken');

const authMiddleware = {
    verifyToken(req, res, next) {
        const token = req.headers['authorization']?.split(' ')[1];
        
        if (!token) {
            return res.status(403).json({ message: 'Token no proporcionado' });
        }
        
        try {
            const decoded = jwt.verify(token, process.env.JWT_SECRET);
            req.userId = decoded.id;
            req.userEmail = decoded.email;
            req.userTipo = decoded.tipo;
            next();
        } catch (error) {
            return res.status(401).json({ message: 'Token inválido' });
        }
    },

    verifyAdmin(req, res, next) {
        if (req.userTipo !== 'admin') {
            return res.status(403).json({ message: 'Acceso no autorizado' });
        }
        next();
    }
};

module.exports = authMiddleware;
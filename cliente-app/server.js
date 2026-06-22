const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// ─── ESB target ──────────────────────────────────────────────────────────────
// Dentro de Docker: http://esb:8080
// En local (dev):   http://localhost:8080
const ESB_URL = process.env.ESB_URL || 'http://esb:8080';

// ─── Proxy /api/* → ESB ──────────────────────────────────────────────────────
// El frontend llama a /api/clientes, /api/productos, /api/pedidos
// Este proxy los reescribe a   /esb/clientes,  /esb/productos,  /esb/pedidos
app.use(
  '/api',
  createProxyMiddleware({
    target: ESB_URL,
    changeOrigin: true,
    pathRewrite: { '^/api': '/esb' },
    on: {
      error: (err, req, res) => {
        console.error('[proxy] error:', err.message);
        res.status(502).json({ error: 'ESB no disponible', detail: err.message });
      },
      proxyReq: (proxyReq, req) => {
        console.log(`[proxy] ${req.method} ${req.path} → ${ESB_URL}/esb${req.path}`);
      },
    },
  })
);

// ─── Archivos estáticos ───────────────────────────────────────────────────────
app.use(express.static(path.join(__dirname, 'public')));

// ─── SPA fallback ─────────────────────────────────────────────────────────────
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`cliente-app corriendo en http://localhost:${PORT}`);
  console.log(`ESB target: ${ESB_URL}`);
});

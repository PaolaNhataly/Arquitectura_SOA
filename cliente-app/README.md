# cliente-app

Frontend SOA que consume **únicamente** el ESB. Nunca llama directo a los servicios SOAP.

## Flujo de red

```
Browser
  │  GET /api/clientes
  ▼
cliente-app :3000   (Express proxy)
  │  reescribe → GET /esb/clientes
  ▼
esb-camel :8080     (Apache Camel)
  │  SOAP/XML
  ▼
servicio-clientes :8081
```

## Endpoints que expone

| Ruta cliente-app | Reescrito a ESB        | Método | Descripción           |
|------------------|------------------------|--------|-----------------------|
| /api/clientes    | /esb/clientes          | GET    | Lista de clientes     |
| /api/clientes/:id| /esb/clientes/:id      | GET    | Cliente por ID        |
| /api/productos   | /esb/productos         | GET    | Catálogo de productos |
| /api/productos/:id| /esb/productos/:id    | GET    | Producto por ID       |
| /api/pedidos     | /esb/pedidos           | GET    | Lista pedidos         |
| /api/pedidos     | /esb/pedidos           | POST   | Crear pedido          |

## Levantar

### Con Docker Compose (recomendado)

Copia el contenido de `docker-compose.fragment.yml` a tu `docker-compose.yml` principal:

```bash
docker compose up --build cliente-app
```

Acceder en: http://localhost:3000

### En local (desarrollo)

```bash
cd cliente-app
npm install

# Apuntar al ESB local
ESB_URL=http://localhost:8080 npm run dev
```

## Variables de entorno

| Variable  | Default             | Descripción                                |
|-----------|---------------------|--------------------------------------------|
| `PORT`    | 3000                | Puerto del servidor Express                |
| `ESB_URL` | http://esb:8080     | URL del ESB (ajustar nombre del servicio)  |

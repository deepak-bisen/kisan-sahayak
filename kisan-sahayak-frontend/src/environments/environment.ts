export const environment = {
  production: false,
  // Base URL of the api-gateway (Spring Cloud Gateway), which routes to kisan-user, etc.
  apiUrl: 'http://localhost:8080/api',
  // Base URL without /api — used for static resource URLs like uploaded images
  baseUrl: 'http://localhost:8080',
};

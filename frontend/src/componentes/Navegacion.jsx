import { NavLink } from 'react-router-dom';
import './Navegacion.css';

export default function Navegacion() {
  return (
    <nav className="navegacion-principal">
      <NavLink
        to="/ordenes"
        className={({ isActive }) => (isActive ? 'activo' : undefined)}
      >
        Órdenes
      </NavLink>

      <NavLink
        to="/clientes"
        className={({ isActive }) => (isActive ? 'activo' : undefined)}
      >
        Clientes
      </NavLink>
    </nav>
  );
}

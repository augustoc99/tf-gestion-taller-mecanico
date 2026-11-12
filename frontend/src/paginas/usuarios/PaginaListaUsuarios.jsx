import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';
import './usuarios.css';

function obtenerMensajeError(error) {
  const data = error?.response?.data;

  if (typeof data === 'string' && data.trim()) {
    return data;
  }

  if (data?.detalle) {
    return data.detalle;
  }

  if (data?.message) {
    return data.message;
  }

  if (data?.error && typeof data.error === 'string') {
    return data.error;
  }

  return 'No se pudo completar la operación.';
}

function etiquetaRol(rol) {
  if (rol === 'RECEPCION_ADMINISTRACION') {
    return 'Recepción / Administración';
  }

  if (rol === 'MECANICO') {
    return 'Mecánico';
  }

  return rol;
}

export default function PaginaListaUsuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [rol, setRol] = useState('');
  const [cargando, setCargando] = useState(true);
  const [cambiandoEstado, setCambiandoEstado] = useState(null);
  const [error, setError] = useState('');

  const cargarUsuarios = useCallback(async (rolSeleccionado = '') => {
    setCargando(true);
    setError('');

    try {
      const response = await api.get('/usuarios', {
        params: rolSeleccionado ? { rol: rolSeleccionado } : {},
      });
      setUsuarios(response.data);
    } catch (err) {
      setError(obtenerMensajeError(err));
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    cargarUsuarios('');
  }, [cargarUsuarios]);

  async function cambiarEstado(usuario) {
    const nuevoEstado = !usuario.activo;
    setCambiandoEstado(usuario.idUsuario);
    setError('');

    try {
      await api.patch(`/usuarios/${usuario.idUsuario}/activo`, {
        activo: nuevoEstado,
      });
      await cargarUsuarios(rol);
    } catch (err) {
      setError(obtenerMensajeError(err));
    } finally {
      setCambiandoEstado(null);
    }
  }

  function manejarFiltro(event) {
    const nuevoRol = event.target.value;
    setRol(nuevoRol);
    cargarUsuarios(nuevoRol);
  }

  return (
    <main className="pagina-usuarios">
      <div className="cabecera-modulo">
        <div>
          <h1>Usuarios</h1>
          <p>Gestión de recepción, administración y mecánicos del taller.</p>
        </div>

        <Link className="boton-enlace" to="/usuarios/nuevo">
          + Nuevo usuario
        </Link>
      </div>

      <div className="filtros-usuarios">
        <label>
          Filtrar por rol
          <select value={rol} onChange={manejarFiltro}>
            <option value="">Todos</option>
            <option value="RECEPCION_ADMINISTRACION">
              Recepción / Administración
            </option>
            <option value="MECANICO">Mecánico</option>
          </select>
        </label>
      </div>

      {error && <p className="mensaje-error">{error}</p>}

      {cargando ? (
        <p>Cargando usuarios...</p>
      ) : usuarios.length === 0 ? (
        <p>No se encontraron usuarios.</p>
      ) : (
        <div className="tabla-responsive">
          <table className="tabla-usuarios">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Email</th>
                <th>Rol</th>
                <th>Estado</th>
                <th>Fecha de creación</th>
                <th></th>
              </tr>
            </thead>

            <tbody>
              {usuarios.map((usuario) => (
                <tr key={usuario.idUsuario}>
                  <td>{usuario.idUsuario}</td>
                  <td>{usuario.nombreCompleto}</td>
                  <td>{usuario.email || '—'}</td>
                  <td>{etiquetaRol(usuario.rol)}</td>
                  <td>{usuario.activo ? 'Activo' : 'Inactivo'}</td>
                  <td>
                    {usuario.fechaCreacion
                      ? new Date(usuario.fechaCreacion).toLocaleString()
                      : '—'}
                  </td>
                  <td className="acciones-usuario">
                    <Link to={`/usuarios/${usuario.idUsuario}/editar`}>
                      Editar
                    </Link>

                    <button
                      type="button"
                      className="boton-texto"
                      disabled={cambiandoEstado === usuario.idUsuario}
                      onClick={() => cambiarEstado(usuario)}
                    >
                      {cambiandoEstado === usuario.idUsuario
                        ? 'Actualizando...'
                        : usuario.activo
                          ? 'Desactivar'
                          : 'Activar'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}

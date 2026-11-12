import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import api from '../../api/axios';
import './usuarios.css';

const formularioVacio = {
  nombreCompleto: '',
  email: '',
  rol: '',
};

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

export default function PaginaFormularioUsuario() {
  const { id } = useParams();
  const navigate = useNavigate();
  const esEdicion = Boolean(id);

  const [formulario, setFormulario] = useState(formularioVacio);
  const [roles, setRoles] = useState([]);
  const [rolActual, setRolActual] = useState('');
  const [cargando, setCargando] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let activo = true;

    async function cargarDatos() {
      setCargando(true);
      setError('');

      try {
        const promesas = [api.get('/usuarios/roles')];

        if (esEdicion) {
          promesas.push(api.get(`/usuarios/${id}`));
        }

        const resultados = await Promise.all(promesas);

        if (!activo) {
          return;
        }

        setRoles(resultados[0].data);

        if (esEdicion) {
          const usuario = resultados[1].data;

          setFormulario({
            nombreCompleto: usuario.nombreCompleto ?? '',
            email: usuario.email ?? '',
            rol: usuario.rol ?? '',
          });
          setRolActual(usuario.rol ?? '');
        }
      } catch (err) {
        if (activo) {
          setError(obtenerMensajeError(err));
        }
      } finally {
        if (activo) {
          setCargando(false);
        }
      }
    }

    cargarDatos();

    return () => {
      activo = false;
    };
  }, [esEdicion, id]);

  function actualizarCampo(event) {
    const { name, value } = event.target;

    setFormulario((actual) => ({
      ...actual,
      [name]: value,
    }));
  }

  async function guardar(event) {
    event.preventDefault();

    if (!formulario.nombreCompleto.trim()) {
      setError('El nombre completo es obligatorio.');
      return;
    }

    if (!esEdicion && !formulario.rol) {
      setError('El rol es obligatorio.');
      return;
    }

    setGuardando(true);
    setError('');

    try {
      if (esEdicion) {
        await api.put(`/usuarios/${id}`, {
          nombreCompleto: formulario.nombreCompleto.trim(),
          email: formulario.email.trim() || null,
        });
      } else {
        await api.post('/usuarios', {
          nombreCompleto: formulario.nombreCompleto.trim(),
          email: formulario.email.trim() || null,
          rol: formulario.rol,
        });
      }

      navigate('/usuarios');
    } catch (err) {
      setError(obtenerMensajeError(err));
    } finally {
      setGuardando(false);
    }
  }

  if (cargando) {
    return (
      <main className="pagina-usuarios">
        <p>Cargando usuario...</p>
      </main>
    );
  }

  return (
    <main className="pagina-usuarios pagina-formulario-usuario">
      <Link to="/usuarios">← Volver a usuarios</Link>

      <h1>{esEdicion ? 'Editar usuario' : 'Nuevo usuario'}</h1>

      {error && <p className="mensaje-error">{error}</p>}

      <form className="formulario-usuario" onSubmit={guardar}>
        <label>
          Nombre completo *
          <input
            name="nombreCompleto"
            value={formulario.nombreCompleto}
            onChange={actualizarCampo}
            maxLength={120}
            required
          />
        </label>

        <label>
          Email
          <input
            name="email"
            type="email"
            value={formulario.email}
            onChange={actualizarCampo}
            maxLength={120}
          />
        </label>

        {esEdicion ? (
          <label>
            Rol
            <input value={etiquetaRol(rolActual)} disabled />
            <small>
              El rol se mantiene estable después de crear el usuario.
            </small>
          </label>
        ) : (
          <label>
            Rol *
            <select
              name="rol"
              value={formulario.rol}
              onChange={actualizarCampo}
              required
            >
              <option value="">Seleccionar rol</option>
              {roles.map((rol) => (
                <option key={rol} value={rol}>
                  {etiquetaRol(rol)}
                </option>
              ))}
            </select>
          </label>
        )}

        <div className="acciones-formulario">
          <button type="submit" disabled={guardando}>
            {guardando
              ? 'Guardando...'
              : esEdicion
                ? 'Guardar cambios'
                : 'Crear usuario'}
          </button>

          <Link to="/usuarios">Cancelar</Link>
        </div>
      </form>
    </main>
  );
}

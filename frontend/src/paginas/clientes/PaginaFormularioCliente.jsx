import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import api from '../../api/axios';
import './clientes.css';

const formularioVacio = {
  nombre: '',
  dniCuit: '',
  telefono: '',
  email: '',
  direccion: '',
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

function normalizarOpcional(valor) {
  const limpio = valor.trim();
  return limpio === '' ? null : limpio;
}

export default function PaginaFormularioCliente() {
  const { id } = useParams();
  const navigate = useNavigate();
  const esEdicion = Boolean(id);

  const [formulario, setFormulario] = useState(formularioVacio);
  const [cargando, setCargando] = useState(esEdicion);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!esEdicion) {
      return;
    }

    let activo = true;

    async function cargarCliente() {
      setCargando(true);
      setError('');

      try {
        const response = await api.get(`/clientes/${id}`);

        if (activo) {
          const cliente = response.data;
          setFormulario({
            nombre: cliente.nombre ?? '',
            dniCuit: cliente.dniCuit ?? '',
            telefono: cliente.telefono ?? '',
            email: cliente.email ?? '',
            direccion: cliente.direccion ?? '',
          });
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

    cargarCliente();

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

    if (!formulario.nombre.trim()) {
      setError('El nombre es obligatorio.');
      return;
    }

    setGuardando(true);
    setError('');

    const payload = {
      nombre: formulario.nombre.trim(),
      dniCuit: normalizarOpcional(formulario.dniCuit),
      telefono: normalizarOpcional(formulario.telefono),
      email: normalizarOpcional(formulario.email),
      direccion: normalizarOpcional(formulario.direccion),
    };

    try {
      if (esEdicion) {
        await api.put(`/clientes/${id}`, payload);
      } else {
        await api.post('/clientes', payload);
      }

      navigate('/clientes');
    } catch (err) {
      setError(obtenerMensajeError(err));
    } finally {
      setGuardando(false);
    }
  }

  if (cargando) {
    return (
      <main className="pagina-clientes">
        <p>Cargando cliente...</p>
      </main>
    );
  }

  return (
    <main className="pagina-clientes pagina-formulario-cliente">
      <Link to="/clientes">← Volver a clientes</Link>

      <h1>{esEdicion ? 'Editar cliente' : 'Nuevo cliente'}</h1>

      {error && <p className="mensaje-error">{error}</p>}

      <form className="formulario-cliente" onSubmit={guardar}>
        <label>
          Nombre *
          <input
            name="nombre"
            value={formulario.nombre}
            onChange={actualizarCampo}
            maxLength={120}
            required
          />
        </label>

        <label>
          DNI/CUIT
          <input
            name="dniCuit"
            value={formulario.dniCuit}
            onChange={actualizarCampo}
            maxLength={20}
          />
        </label>

        <label>
          Teléfono
          <input
            name="telefono"
            value={formulario.telefono}
            onChange={actualizarCampo}
            maxLength={30}
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

        <label>
          Dirección
          <input
            name="direccion"
            value={formulario.direccion}
            onChange={actualizarCampo}
            maxLength={200}
          />
        </label>

        <div className="acciones-formulario">
          <button type="submit" disabled={guardando}>
            {guardando
              ? 'Guardando...'
              : esEdicion
                ? 'Guardar cambios'
                : 'Crear cliente'}
          </button>

          <Link to="/clientes">Cancelar</Link>
        </div>
      </form>
    </main>
  );
}

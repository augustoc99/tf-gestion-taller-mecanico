import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../../api/axios';

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

  return error?.message || 'No se pudo completar la operación.';
}

export default function PaginaCrearOrden() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    idVehiculo: '',
    idMecanico: '',
    kilometrajeIngreso: '',
    motivoIngreso: '',
    observaciones: '',
    idUsuarioResponsable: '',
  });

  const [vehiculos, setVehiculos] = useState([]);
  const [mecanicos, setMecanicos] = useState([]);
  const [recepcion, setRecepcion] = useState([]);
  const [cargandoDatos, setCargandoDatos] = useState(true);
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let activo = true;

    async function cargarDatos() {
      setCargandoDatos(true);
      setError(null);

      try {
        const [vehiculosResponse, mecanicosResponse, recepcionResponse] = await Promise.all([
          api.get('/vehiculos'),
          api.get('/usuarios/mecanicos'),
          api.get('/usuarios', {
            params: { rol: 'RECEPCION_ADMINISTRACION' },
          }),
        ]);

        if (!activo) {
          return;
        }

        const usuariosRecepcionActivos = recepcionResponse.data.filter(
          (usuario) => usuario.activo,
        );

        setVehiculos(vehiculosResponse.data);
        setMecanicos(mecanicosResponse.data);
        setRecepcion(usuariosRecepcionActivos);

        if (usuariosRecepcionActivos.length > 0) {
          setForm((actual) => ({
            ...actual,
            idUsuarioResponsable: String(usuariosRecepcionActivos[0].idUsuario),
          }));
        }
      } catch (err) {
        if (activo) {
          setError(obtenerMensajeError(err));
        }
      } finally {
        if (activo) {
          setCargandoDatos(false);
        }
      }
    }

    cargarDatos();

    return () => {
      activo = false;
    };
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((actual) => ({
      ...actual,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError(null);

    if (!form.idVehiculo) {
      setError('Debe seleccionar un vehículo.');
      return;
    }

    if (!form.idUsuarioResponsable) {
      setError('Debe seleccionar un responsable de recepción.');
      return;
    }

    setEnviando(true);

    try {
      const body = {
        idVehiculo: Number(form.idVehiculo),
        idMecanico: form.idMecanico ? Number(form.idMecanico) : null,
        kilometrajeIngreso: Number(form.kilometrajeIngreso),
        motivoIngreso: form.motivoIngreso.trim(),
        observaciones: form.observaciones.trim() || null,
        idUsuarioResponsable: Number(form.idUsuarioResponsable),
      };

      const { data } = await api.post('/ordenes', body);
      navigate(`/ordenes/${data.idOrden}`);
    } catch (err) {
      setError(obtenerMensajeError(err));
    } finally {
      setEnviando(false);
    }
  }

  if (cargandoDatos) {
    return <p style={{ padding: 20 }}>Cargando datos...</p>;
  }

  return (
    <div style={{ padding: 20, maxWidth: 650, margin: '0 auto' }}>
      <Link to="/ordenes">← Volver a la lista</Link>
      <h1>Nueva orden de trabajo</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 12 }}>
          <label htmlFor="idVehiculo">Vehículo *</label><br />
          <select
            id="idVehiculo"
            name="idVehiculo"
            value={form.idVehiculo}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: 7 }}
          >
            <option value="">Seleccionar vehículo</option>
            {vehiculos.map((vehiculo) => (
              <option key={vehiculo.idVehiculo} value={vehiculo.idVehiculo}>
                {vehiculo.patente} — {vehiculo.marca} {vehiculo.modelo} — {vehiculo.nombreCliente}
              </option>
            ))}
          </select>
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="idMecanico">Mecánico (opcional)</label><br />
          <select
            id="idMecanico"
            name="idMecanico"
            value={form.idMecanico}
            onChange={handleChange}
            style={{ width: '100%', padding: 7 }}
          >
            <option value="">Sin asignar por ahora</option>
            {mecanicos.map((mecanico) => (
              <option key={mecanico.idUsuario} value={mecanico.idUsuario}>
                {mecanico.nombreCompleto}
              </option>
            ))}
          </select>
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="idUsuarioResponsable">Responsable de recepción *</label><br />
          <select
            id="idUsuarioResponsable"
            name="idUsuarioResponsable"
            value={form.idUsuarioResponsable}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: 7 }}
          >
            <option value="">Seleccionar responsable</option>
            {recepcion.map((usuario) => (
              <option key={usuario.idUsuario} value={usuario.idUsuario}>
                {usuario.nombreCompleto}
              </option>
            ))}
          </select>
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="kilometrajeIngreso">Kilometraje de ingreso *</label><br />
          <input
            id="kilometrajeIngreso"
            type="number"
            name="kilometrajeIngreso"
            value={form.kilometrajeIngreso}
            onChange={handleChange}
            required
            min="0"
            style={{ width: '100%', padding: 6 }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="motivoIngreso">Motivo de ingreso * (máx. 1000)</label><br />
          <textarea
            id="motivoIngreso"
            name="motivoIngreso"
            value={form.motivoIngreso}
            onChange={handleChange}
            required
            maxLength={1000}
            rows={3}
            style={{ width: '100%', padding: 6 }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="observaciones">Observaciones (máx. 2000)</label><br />
          <textarea
            id="observaciones"
            name="observaciones"
            value={form.observaciones}
            onChange={handleChange}
            maxLength={2000}
            rows={3}
            style={{ width: '100%', padding: 6 }}
          />
        </div>

        <button type="submit" disabled={enviando} style={{ padding: '8px 16px' }}>
          {enviando ? 'Creando...' : 'Crear orden'}
        </button>
      </form>
    </div>
  );
}

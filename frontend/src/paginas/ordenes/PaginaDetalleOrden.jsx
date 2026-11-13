import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import api from '../../api/axios';
import FormTrabajo from '../../componentes/ordenes/FormTrabajo';
import SelectorEstado from '../../componentes/ordenes/SelectorEstado';

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

function etiquetaRol(rol) {
  if (rol === 'RECEPCION_ADMINISTRACION') {
    return 'Recepción / Administración';
  }

  if (rol === 'MECANICO') {
    return 'Mecánico';
  }

  return rol;
}

export default function PaginaDetalleOrden() {
  const { id } = useParams();

  const [orden, setOrden] = useState(null);
  const [historial, setHistorial] = useState([]);
  const [trabajos, setTrabajos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [mecanicos, setMecanicos] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [mensaje, setMensaje] = useState(null);

  const [diagnostico, setDiagnostico] = useState('');
  const [obsTecnica, setObsTecnica] = useState('');
  const [idMecanico, setIdMecanico] = useState('');
  const [idUsuarioResponsable, setIdUsuarioResponsable] = useState('');

  const recargar = async () => {
    const [o, h, t] = await Promise.all([
      api.get(`/ordenes/${id}`),
      api.get(`/ordenes/${id}/historial`),
      api.get(`/ordenes/${id}/trabajos`),
    ]);

    setOrden(o.data);
    setHistorial(h.data);
    setTrabajos(t.data);
    setDiagnostico(o.data.diagnostico || '');

    if (o.data.idMecanico) {
      setIdMecanico(String(o.data.idMecanico));
    }
  };

  useEffect(() => {
    let activo = true;

    Promise.all([
      api.get(`/ordenes/${id}`),
      api.get(`/ordenes/${id}/historial`),
      api.get(`/ordenes/${id}/trabajos`),
      api.get('/usuarios'),
      api.get('/usuarios/mecanicos'),
    ])
      .then(([o, h, t, u, m]) => {
        if (!activo) {
          return;
        }

        const usuariosActivos = u.data.filter((usuario) => usuario.activo);

        setOrden(o.data);
        setHistorial(h.data);
        setTrabajos(t.data);
        setUsuarios(usuariosActivos);
        setMecanicos(m.data);
        setDiagnostico(o.data.diagnostico || '');

        if (o.data.idMecanico) {
          setIdMecanico(String(o.data.idMecanico));
        }

        const mecanicoAsignadoActivo = usuariosActivos.find(
          (usuario) => usuario.idUsuario === o.data.idMecanico && usuario.rol === 'MECANICO',
        );

        const recepcionActiva = usuariosActivos.find(
          (usuario) => usuario.rol === 'RECEPCION_ADMINISTRACION',
        );

        const responsableInicial = mecanicoAsignadoActivo || recepcionActiva || usuariosActivos[0];

        if (responsableInicial) {
          setIdUsuarioResponsable(String(responsableInicial.idUsuario));
        }
      })
      .catch((err) => {
        if (activo) {
          setError(obtenerMensajeError(err));
        }
      })
      .finally(() => {
        if (activo) {
          setLoading(false);
        }
      });

    return () => {
      activo = false;
    };
  }, [id]);

  async function ejecutar(fn, exito) {
    setError(null);
    setMensaje(null);

    if (!idUsuarioResponsable) {
      setError('Seleccioná el usuario actual antes de realizar la acción.');
      return;
    }

    try {
      await fn();
      await recargar();
      setMensaje(exito);
    } catch (err) {
      setError(obtenerMensajeError(err));
    }
  }

  const cambiarEstado = (estadoNuevo, observacion) =>
    ejecutar(
      () => api.put(`/ordenes/${id}/estado`, {
        estadoNuevo,
        idUsuarioResponsable: Number(idUsuarioResponsable),
        observacion: observacion || null,
      }),
      `Estado cambiado a ${estadoNuevo.replaceAll('_', ' ')}`,
    );

  const agregarTrabajo = (descripcion) =>
    ejecutar(
      () => api.post(`/ordenes/${id}/trabajos`, {
        descripcion,
        idUsuarioResponsable: Number(idUsuarioResponsable),
      }),
      'Trabajo agregado',
    );

  function guardarDiagnostico(event) {
    event.preventDefault();

    if (!diagnostico.trim()) {
      return;
    }

    ejecutar(
      () => api.put(`/ordenes/${id}/tecnica`, {
        diagnostico: diagnostico.trim(),
        observaciones: obsTecnica.trim() || null,
        idUsuarioResponsable: Number(idUsuarioResponsable),
      }),
      'Diagnóstico guardado',
    );
  }

  function asignarMecanico(event) {
    event.preventDefault();

    if (!idMecanico) {
      return;
    }

    ejecutar(
      () => api.put(`/ordenes/${id}/mecanico`, {
        idMecanico: Number(idMecanico),
        idUsuarioResponsable: Number(idUsuarioResponsable),
      }),
      'Mecánico asignado',
    );
  }

  if (loading) {
    return <p style={{ padding: 20 }}>Cargando...</p>;
  }

  if (error && !orden) {
    return <p style={{ padding: 20, color: 'red' }}>Error: {error}</p>;
  }

  if (!orden) {
    return <p style={{ padding: 20 }}>Orden no encontrada.</p>;
  }

  const entregada = orden.estado === 'ENTREGADO';

  const puedeRegistrarTrabajo =
    ['EN_REPARACION', 'CONTROL_FINAL'].includes(orden.estado)
    && Number(idUsuarioResponsable) === Number(orden.idMecanico);

  return (
    <div style={{ padding: 20, maxWidth: 900, margin: '0 auto' }}>
      <Link to="/ordenes">← Volver a la lista</Link>
      <h1>Orden #{orden.idOrden} — {orden.patenteVehiculo}</h1>

      {mensaje && <p style={{ color: 'green' }}>{mensaje}</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Usuario actual</h2>
        <p style={{ marginTop: 0 }}>
          Seleccioná quién realiza las acciones para que el backend aplique las reglas de rol.
        </p>

        <select
          value={idUsuarioResponsable}
          onChange={(event) => setIdUsuarioResponsable(event.target.value)}
          style={{ width: '100%', padding: 7 }}
        >
          <option value="">Seleccionar usuario</option>
          {usuarios.map((usuario) => (
            <option key={usuario.idUsuario} value={usuario.idUsuario}>
              {usuario.nombreCompleto} — {etiquetaRol(usuario.rol)}
            </option>
          ))}
        </select>
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Datos</h2>
        <p><b>Estado:</b> {orden.estado.replaceAll('_', ' ')}</p>
        <p><b>Vehículo:</b> {orden.patenteVehiculo}</p>
        <p><b>Cliente:</b> {orden.nombreCliente}</p>
        <p><b>Mecánico:</b> {orden.nombreMecanico ?? 'sin asignar'}</p>
        <p><b>Kilometraje ingreso:</b> {orden.kilometrajeIngreso ?? '—'}</p>
        <p><b>Motivo:</b> {orden.motivoIngreso ?? '—'}</p>
        <p><b>Diagnóstico:</b> {orden.diagnostico ?? '—'}</p>
        <p><b>Observaciones:</b> {orden.observaciones ?? '—'}</p>
        <p><b>Fecha ingreso:</b> {orden.fechaIngreso?.replace('T', ' ').slice(0, 16) ?? '—'}</p>
        <p><b>Fecha entrega:</b> {orden.fechaEntrega?.replace('T', ' ').slice(0, 16) ?? '—'}</p>
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Cambiar estado</h2>
        <SelectorEstado
          estadoActual={orden.estado}
          onCambiar={cambiarEstado}
          deshabilitado={entregada || !idUsuarioResponsable}
        />
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Diagnóstico técnico</h2>
        <form onSubmit={guardarDiagnostico}>
          <textarea
            placeholder="Diagnóstico"
            value={diagnostico}
            onChange={(event) => setDiagnostico(event.target.value)}
            disabled={entregada}
            rows={3}
            style={{ width: '100%', padding: 6, marginBottom: 8 }}
          />

          <input
            type="text"
            placeholder="Observaciones (opcional)"
            value={obsTecnica}
            onChange={(event) => setObsTecnica(event.target.value)}
            disabled={entregada}
            style={{ width: '100%', padding: 6, marginBottom: 8 }}
          />

          <button
            type="submit"
            disabled={entregada || !diagnostico.trim() || !idUsuarioResponsable}
          >
            Guardar diagnóstico
          </button>
        </form>
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Mecánico asignado</h2>
        <form onSubmit={asignarMecanico}>
          <select
            value={idMecanico}
            onChange={(event) => setIdMecanico(event.target.value)}
            disabled={entregada}
            style={{ padding: 7, marginRight: 8, minWidth: 280 }}
          >
            <option value="">Seleccionar mecánico</option>
            {mecanicos.map((mecanico) => (
              <option key={mecanico.idUsuario} value={mecanico.idUsuario}>
                {mecanico.nombreCompleto}
              </option>
            ))}
          </select>

          <button
            type="submit"
            disabled={entregada || !idMecanico || !idUsuarioResponsable}
          >
            Asignar
          </button>
        </form>
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Trabajos realizados</h2>

        {trabajos.length === 0 ? (
          <p>Sin trabajos cargados.</p>
        ) : (
          <ul>
            {trabajos.map((trabajo) => (
              <li key={trabajo.idDetalle}>
                {trabajo.descripcion}
                {trabajo.fechaRegistro && (
                  <span style={{ color: '#888', marginLeft: 8, fontSize: 12 }}>
                    ({trabajo.fechaRegistro.replace('T', ' ').slice(0, 16)})
                  </span>
                )}
              </li>
            ))}
          </ul>
        )}

        {!puedeRegistrarTrabajo && !entregada && (
          <p style={{ color: '#666', fontSize: 13 }}>
            Los trabajos pueden registrarse en EN REPARACION o CONTROL FINAL
            por el mecánico asignado.
          </p>
        )}

        <FormTrabajo
          onAgregar={agregarTrabajo}
          deshabilitado={!puedeRegistrarTrabajo}
        />
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12 }}>
        <h2>Historial de estados</h2>

        {historial.length === 0 ? (
          <p>Sin cambios registrados.</p>
        ) : (
          <table
            border="1"
            cellPadding="8"
            style={{ borderCollapse: 'collapse', width: '100%' }}
          >
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Desde</th>
                <th>Hacia</th>
                <th>Usuario</th>
                <th>Observación</th>
              </tr>
            </thead>

            <tbody>
              {historial.map((item) => (
                <tr key={item.idHistorial}>
                  <td>{item.fechaCambio?.replace('T', ' ').slice(0, 16) ?? '—'}</td>
                  <td>{item.estadoAnterior?.replaceAll('_', ' ') ?? '—'}</td>
                  <td>{item.estadoNuevo.replaceAll('_', ' ')}</td>
                  <td>{item.nombreUsuario} ({item.rolUsuario})</td>
                  <td>{item.observacion ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}

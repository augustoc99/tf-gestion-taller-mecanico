import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/axios';
import SelectorEstado from '../../componentes/ordenes/SelectorEstado';
import FormTrabajo from '../../componentes/ordenes/FormTrabajo';

const ID_USUARIO_RESPONSABLE = 1;

export default function PaginaDetalleOrden() {
  const { id } = useParams();
  const [orden, setOrden] = useState(null);
  const [historial, setHistorial] = useState([]);
  const [trabajos, setTrabajos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [mensaje, setMensaje] = useState(null);

  const [diagnostico, setDiagnostico] = useState('');
  const [obsTecnica, setObsTecnica] = useState('');
  const [idMecanico, setIdMecanico] = useState('');

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
  };

  useEffect(() => {
    let activo = true;
    Promise.all([
      api.get(`/ordenes/${id}`),
      api.get(`/ordenes/${id}/historial`),
      api.get(`/ordenes/${id}/trabajos`),
    ])
      .then(([o, h, t]) => {
        if (!activo) return;
        setOrden(o.data);
        setHistorial(h.data);
        setTrabajos(t.data);
        setDiagnostico(o.data.diagnostico || '');
      })
      .catch((e) => {
        if (activo) setError(e.response?.data?.detalle || e.message);
      })
      .finally(() => {
        if (activo) setLoading(false);
      });
    return () => { activo = false; };
  }, [id]);

  const ejecutar = async (fn, exito) => {
    setError(null);
    setMensaje(null);
    try {
      await fn();
      await recargar();
      setMensaje(exito);
    } catch (e) {
      setError(e.response?.data?.detalle || e.message);
    }
  };

  const cambiarEstado = (estadoNuevo, observacion) =>
    ejecutar(
      () => api.put(`/ordenes/${id}/estado`, {
        estadoNuevo,
        idUsuarioResponsable: ID_USUARIO_RESPONSABLE,
        observacion: observacion || null,
      }),
      `Estado cambiado a ${estadoNuevo.replaceAll('_', ' ')}`
    );

  const agregarTrabajo = (descripcion) =>
    ejecutar(
      () => api.post(`/ordenes/${id}/trabajos`, {
        descripcion,
        idUsuarioResponsable: ID_USUARIO_RESPONSABLE,
      }),
      'Trabajo agregado'
    );

  const guardarDiagnostico = (e) => {
    e.preventDefault();
    if (!diagnostico.trim()) return;
    ejecutar(
      () => api.put(`/ordenes/${id}/tecnica`, {
        diagnostico: diagnostico.trim(),
        observaciones: obsTecnica || null,
        idUsuarioResponsable: ID_USUARIO_RESPONSABLE,
      }),
      'Diagnóstico guardado'
    );
  };

  const asignarMecanico = (e) => {
    e.preventDefault();
    if (!idMecanico) return;
    ejecutar(
      () => api.put(`/ordenes/${id}/mecanico`, {
        idMecanico: Number(idMecanico),
        idUsuarioResponsable: ID_USUARIO_RESPONSABLE,
      }),
      'Mecánico asignado'
    );
  };

  if (loading) return <p style={{ padding: 20 }}>Cargando...</p>;
  if (error && !orden) return <p style={{ padding: 20, color: 'red' }}>Error: {error}</p>;
  if (!orden) return <p style={{ padding: 20 }}>Orden no encontrada.</p>;

  const entregada = orden.estado === 'ENTREGADO';

  return (
    <div style={{ padding: 20, maxWidth: 900, margin: '0 auto' }}>
      <Link to="/ordenes">← Volver a la lista</Link>
      <h1>Orden #{orden.idOrden} — {orden.patenteVehiculo}</h1>

      {mensaje && <p style={{ color: 'green' }}>{mensaje}</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Datos</h2>
        <p><b>Estado:</b> {orden.estado.replaceAll('_', ' ')}</p>
        <p><b>Vehículo:</b> {orden.patenteVehiculo} (id {orden.idVehiculo})</p>
        <p><b>Cliente:</b> {orden.nombreCliente} (id {orden.idCliente})</p>
        <p><b>Mecánico:</b> {orden.nombreMecanico ?? 'sin asignar'}{orden.idMecanico ? ` (id ${orden.idMecanico})` : ''}</p>
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
          deshabilitado={entregada}
        />
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Diagnóstico técnico</h2>
        <form onSubmit={guardarDiagnostico}>
          <textarea
            placeholder="Diagnóstico"
            value={diagnostico}
            onChange={(e) => setDiagnostico(e.target.value)}
            disabled={entregada}
            rows={3}
            style={{ width: '100%', padding: 6, marginBottom: 8 }}
          />
          <input
            type="text"
            placeholder="Observaciones (opcional)"
            value={obsTecnica}
            onChange={(e) => setObsTecnica(e.target.value)}
            disabled={entregada}
            style={{ width: '100%', padding: 6, marginBottom: 8 }}
          />
          <button type="submit" disabled={entregada || !diagnostico.trim()}>
            Guardar diagnóstico
          </button>
        </form>
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12, marginBottom: 16 }}>
        <h2>Mecánico asignado</h2>
        <form onSubmit={asignarMecanico}>
          <input
            type="number"
            placeholder="ID del mecánico"
            value={idMecanico}
            onChange={(e) => setIdMecanico(e.target.value)}
            disabled={entregada}
            style={{ padding: 6, marginRight: 8, width: 160 }}
          />
          <button type="submit" disabled={entregada || !idMecanico}>
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
            {trabajos.map((t) => (
              <li key={t.idDetalle}>
                {t.descripcion}
                {t.fechaRegistro && (
                  <span style={{ color: '#888', marginLeft: 8, fontSize: 12 }}>
                    ({t.fechaRegistro.replace('T', ' ').slice(0, 16)})
                  </span>
                )}
              </li>
            ))}
          </ul>
        )}
        <FormTrabajo onAgregar={agregarTrabajo} deshabilitado={entregada} />
      </section>

      <section style={{ border: '1px solid #ddd', padding: 12 }}>
        <h2>Historial de estados</h2>
        {historial.length === 0 ? (
          <p>Sin cambios registrados.</p>
        ) : (
          <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse', width: '100%' }}>
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
              {historial.map((h) => (
                <tr key={h.idHistorial}>
                  <td>{h.fechaCambio?.replace('T', ' ').slice(0, 16) ?? '—'}</td>
                  <td>{h.estadoAnterior?.replaceAll('_', ' ') ?? '—'}</td>
                  <td>{h.estadoNuevo.replaceAll('_', ' ')}</td>
                  <td>{h.nombreUsuario} ({h.rolUsuario})</td>
                  <td>{h.observacion ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}
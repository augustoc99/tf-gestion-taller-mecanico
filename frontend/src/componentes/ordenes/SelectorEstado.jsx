import { useState } from 'react';

const TRANSICIONES = {
  RECIBIDO: ['EN_DIAGNOSTICO'],
  EN_DIAGNOSTICO: ['EN_REPARACION', 'ESPERANDO_REPUESTO'],
  EN_REPARACION: ['ESPERANDO_REPUESTO', 'CONTROL_FINAL'],
  ESPERANDO_REPUESTO: ['EN_REPARACION'],
  CONTROL_FINAL: ['EN_REPARACION', 'FINALIZADO'],
  FINALIZADO: ['ENTREGADO'],
  ENTREGADO: [],
};

export default function SelectorEstado({ estadoActual, onCambiar, deshabilitado }) {
  const [observacion, setObservacion] = useState('');
  const [enviando, setEnviando] = useState(false);

  const opciones = TRANSICIONES[estadoActual] || [];

  if (opciones.length === 0) {
    return (
      <p style={{ color: '#666' }}>
        La orden está en estado <b>{estadoActual}</b>. No admite más cambios.
      </p>
    );
  }

  const handleClick = async (nuevoEstado) => {
    setEnviando(true);
    try {
      await onCambiar(nuevoEstado, observacion);
      setObservacion('');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 8 }}>
        <input
          type="text"
          placeholder="Observación (opcional)"
          value={observacion}
          onChange={(e) => setObservacion(e.target.value)}
          disabled={deshabilitado || enviando}
          style={{ width: '100%', padding: 6, marginBottom: 8 }}
        />
        {opciones.map((op) => (
          <button
            key={op}
            onClick={() => handleClick(op)}
            disabled={deshabilitado || enviando}
            style={{ marginRight: 8, marginBottom: 4, padding: '6px 12px' }}
          >
            → {op.replaceAll('_', ' ')}
          </button>
        ))}
      </div>
    </div>
  );
}
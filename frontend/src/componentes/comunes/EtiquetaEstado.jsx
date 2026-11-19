const ESTADO_CONFIG = {
  RECIBIDO:           { bg: '#E5E7EB', text: '#374151' },
  EN_DIAGNOSTICO:     { bg: '#DBEAFE', text: '#1565C0' },
  EN_REPARACION:      { bg: '#FFEDD5', text: '#C2410C' },
  ESPERANDO_REPUESTO: { bg: '#FEF3C7', text: '#B45309' },
  CONTROL_FINAL:      { bg: '#F3E8FF', text: '#6A1B9A' },
  FINALIZADO:         { bg: '#DCFCE7', text: '#166534' },
  ENTREGADO:          { bg: '#E5E7EB', text: '#1F2937' },
};

export default function EtiquetaEstado({ estado }) {
  if (!estado) return null;
  const c = ESTADO_CONFIG[estado] || { bg: '#E5E7EB', text: '#374151' };
  return (
    <span
      style={{
        display: 'inline-block',
        fontFamily: "'IBM Plex Mono', monospace",
        fontSize: 11,
        fontWeight: 700,
        letterSpacing: '0.05em',
        padding: '3px 8px',
        borderRadius: 4,
        backgroundColor: c.bg,
        color: c.text,
        textTransform: 'uppercase',
        whiteSpace: 'nowrap',
      }}
    >
      {estado.replaceAll('_', ' ')}
    </span>
  );
}
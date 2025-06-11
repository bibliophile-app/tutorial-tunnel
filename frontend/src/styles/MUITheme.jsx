import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  spacing: 8,

  palette: {
    primary:   { main: '#2D4857' },  // azul petróleo escuro
    secondary: { main: '#7BA1B9' },  // azul acinzentado leve
    
    background: {
      default: '#1A242F',   // fundo geral (petróleo escuro)
      surface: '#0F151B',   // navbar, painéis
      contrast: '#556677',  // botões, inputs elevados
      muted:   '#44515C',   // divisores, ícones passivos
    },

    neutral: {
      main: '#CFD8DC',              // cinza claro azulado (texto)
      secondary: '#F0F4F8',         // branco azulado para contraste
    },

    error:   { main: '#FF4C4C' },
    warning: { main: '#FFB347' },
    success: { main: '#00e054' },
    info:    { main: '#40bcf4' },

    white:  { main: '#FFFFFF' },
    black:  { main: '#000000' },
    red:    { main: '#FF4C4C' },
    yellow: { main: '#FFD700' },
    orange: { main: '#FFA726' },
    brown:  { main: '#8E7143' },
    green:  { main: '#4CE894' },
    cyan:   { main: '#4CCCD3' },
    pink:   { main: '#E06BB0' },
    purple: {
      main: '#B38BFF',
      secondary: '#9F8BFF',
    }

  },

  shape: {
    borderRadius: 8,
  },

  typography: {
    fontFamily: "'Inter', 'Helvetica Neue', sans-serif",
    allVariants: {
      color: '#CFD8DC',
    },
    h1: { fontSize: '2rem', fontWeight: 800, lineHeight: '2.5rem' },
    h2: { fontSize: '1.75rem', fontWeight: 700, lineHeight: '2.25rem' },
    h3: { fontSize: '1.5rem', fontWeight: 600, lineHeight: '2rem' },
    h4: { fontSize: '1.25rem', fontWeight: 500, lineHeight: '1.75rem' },
    h5: { fontSize: '1rem', fontWeight: 500, lineHeight: '1.5rem' },
    p:  { fontSize: '0.875rem', fontWeight: 400, lineHeight: '1.25rem' },
    small: { fontSize: '0.75rem', fontWeight: 400, lineHeight: '1rem' },
    logo: {
      fontFamily: "'Libre Baskerville', serif",
      fontWeight: 600,
      color: '#CFD8DC',
    },
  },

  components: {
    MuiButton: {
      styleOverrides: {
        root: ({ theme }) => ({
          textTransform: 'none',
          transition: theme.transitions.create(
            ['border-color', 'background-color', 'transform', 'color'],
            { duration: theme.transitions.duration.short }
          ),
          '&:hover': {
            transform: 'scale(1.02)',
          },
          '&:active': {
            transform: 'scale(0.98)',
          },
        }),
      },
      defaultProps: {
        disableElevation: true,
        variant: 'contained',
        color: 'transparent',
      },
    },
  },
});

export default theme;

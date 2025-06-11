import App from './App';
import React from 'react';
import ReactDOM from 'react-dom/client';

import './styles/global.css';
import theme from './styles/MUITheme'; 

import { ThemeProvider } from '@mui/material/styles';
import { AuthProvider } from "./utils/AuthContext";

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <AuthProvider>
        <App />
      </AuthProvider>
    </ThemeProvider>
  </React.StrictMode>,
);
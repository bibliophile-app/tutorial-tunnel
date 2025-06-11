import Icon from '../assets/icon.svg?react';
import { Typography, Box } from "@mui/material";

const sizeMap = {
  sm: "1.5",
  md: "1.7",
  lg: "2.0",
};

function Logo({ size = "md", variant = "full" }) {
  const fontSize = sizeMap[size];
  const iconSize = `calc(${parseFloat(fontSize) + 0.2}rem)`; // Calcular o tamanho do ícone com base no rem

  return (
    <Box display="flex" alignItems="baseline" gap={1}>
      <Icon style={{ height: iconSize, width: iconSize }} />
      {variant === "full" && (
        <Typography
          variant="logo"
          sx={{ fontSize: `${fontSize}rem` }}
        >
          Bibliophile
        </Typography>
      )}
    </Box>
  );
};

export default Logo;
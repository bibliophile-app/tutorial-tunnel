import { Box, Button, Divider, List, ListItem, Typography } from '@mui/material';

const CATEGORIES = ['Livros', 'Listas', 'Usuários'];

function Categories({ selected, onSelect }) {
  return (
    <Box>
      <Typography variant="body" fontSize={"0.8rem"} gutterBottom> 
          FILTRAR RESULTADOS POR
      </Typography>

      <Divider sx={{ mt: 2, bgcolor: "background.muted" }} />

      <List dense>
        {CATEGORIES.map((category) => (
          <ListItem key={category} disablePadding>
            <Button
              fullWidth
              onClick={() => onSelect(category)}
              sx={{
                justifyContent: 'flex-start',
                color: selected === category ? 'white' : 'secondary.main',
                fontWeight: selected === category ? 600 : 400,
                textTransform: 'none',
              }}
            >
              {category}
            </Button>
          </ListItem>
        ))}
      </List>
    </Box>
  );
}

export default Categories;

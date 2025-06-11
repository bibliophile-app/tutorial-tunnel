import { Container } from '@mui/material';

function CustomContainer({ children, ...props }) {
  return (
    <Container disableGutters maxWidth={false} {...props}>
      {children}
    </Container>
  );
}

export default CustomContainer;
import { styled } from '@mui/material/styles';
import { Divider } from '@mui/material';

const StyledDivider = styled(Divider)(({ theme }) => ({
	my: 0.5, 
	backgroundColor: theme.palette.background.muted, 
}));

export default StyledDivider;
import React from 'react';
import { Link } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import { Box, AppBar, Toolbar } from '@mui/material';

import Logo from '../../atoms/Logo';
import MobileToolbar from './MobileToolbar';
import DesktopToolbar from './DesktopToolbar';
import { useAuth } from '../../utils/AuthContext';

const StyledToolbar = styled(Toolbar)(({ theme }) => ({
	display: 'flex',
	justifyContent: 'center',
	backgroundColor: theme.palette.background.surface,
	boxShadow: theme.shadows[4],
	padding: '8px 15px',
}));

const ToolbarContent = styled(Box)(({ theme }) => ({
	display: 'flex',
	alignItems: 'center',
	justifyContent: 'space-between',
	width: '100%',
	maxWidth: '1200px',
	marginLeft: 'auto',
	marginRight: 'auto',
	px: theme.spacing(2),
  
	[theme.breakpoints.up('md')]: {
	  px: theme.spacing(3),
	},
}));  

function NavBar() {
	const { user } = useAuth();
	
	const publicOptions = [];
	const privateOptions = [
		{ name: 'Perfil', path: `${user?.username}/profile` },
		{ name: 'Resenhas', path: `${user?.username}/reviews` },
		{ name: 'Diário', path: `${user?.username}/diary` },
		{ name: 'Quero Ler', path: '/read-next' },
		{ name: 'Listas', path: '/lists' },
	];

	return (
		<AppBar
			sx={{ position: 'static', boxShadow: 0 }}
		>
			<StyledToolbar disableGutters>
				<ToolbarContent>
					{/* Logo on the left */}
					<Box sx={{ display: 'flex', alignItems: 'center' }}>
						<Link to="/" style={{ textDecoration: 'none' }}>
							<Logo variant="full" />
						</Link>
					</Box>
	
					{/* Desktop Navigation */}
					<Box sx={{ display: { xs: 'none', md: 'flex' }, alignItems: 'center', gap: 1 }}>
						<DesktopToolbar user={user} options={user ? privateOptions : publicOptions} />
					</Box>
			
					{/* Mobile Navigation */}
					<Box sx={{ display: { xs: 'flex', md: 'none' }, alignItems: 'center', gap: 1 }}>
						<MobileToolbar user={user} options={user ? privateOptions : publicOptions} />
					</Box>
				</ToolbarContent>
			</StyledToolbar>
		</AppBar>
	);
}

export default NavBar;
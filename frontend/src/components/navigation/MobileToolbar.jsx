import React, {useState} from 'react';
import { useAuth } from '../../utils/AuthContext';
import { Link, useNavigate } from 'react-router-dom';

import { styled } from '@mui/material/styles';
import { Box, Button, Drawer, IconButton, MenuItem } from '@mui/material';

import ListIcon from '@mui/icons-material/List';
import CloseIcon from '@mui/icons-material/Close';
import LogoutIcon from '@mui/icons-material/Logout';

import Logo from '../../atoms/Logo';
import Divider from '../../atoms/Divider';
import SearchBar from '../search/SearchBar';

const StyledMenuItem = styled(MenuItem)(({
	padding: '6px 3px',
}));

// Component for rendering drawer content
function MobileToolbar({ user, options }) {
	const { handleSignin, handleSignup, logout } = useAuth();
	const navigate = useNavigate();
	const [open, setOpen] = useState(false);

	function toggleDrawer(open) {
		return (event) => {
    		if (event && event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      			return;
    		}
    		setOpen(open);
		};
	}

	return (
		<React.Fragment>
			<SearchBar />
			<IconButton aria-label="Menu button" onClick={toggleDrawer(true)}>
				<ListIcon fontSize="medium" sx={{ color: 'neutral.main' }} />
			</IconButton>
			<Drawer anchor="top" open={open} onClose={toggleDrawer(false)}>
				<Box sx={{ p: 2, backgroundColor: 'background.surface', height: '100%' }}>
					<Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
						<Link to="/" style={{ textDecoration: 'none' }}>
							<Logo variant="icon" size="sm"/>
						</Link>
						<Box sx={{ display: 'flex', alignItems: 'center' }}> 
							<SearchBar />
							<IconButton aria-label="Menu button" onClick={toggleDrawer(false)}>
								<CloseIcon fontSize="medium" sx={{ color: 'neutral.main' }} />
							</IconButton>
						</Box>
					</Box>
					
					{!user && (
						<React.Fragment>
							<StyledMenuItem onClick={handleSignin}> Login </StyledMenuItem>
							<Divider />
							<StyledMenuItem onClick={handleSignup}> Criar Conta </StyledMenuItem>
							<Divider />
						</React.Fragment>
					)}
					
					{options.map((option, index) => (
						<React.Fragment key={option.name}>
							{index !== 0 && <Divider />}
							<StyledMenuItem component={Link} to={option.path} onClick={toggleDrawer(false)}>
								{option.icon && <option.icon fontSize="small" />}
								{option.name}
							</StyledMenuItem>
						</React.Fragment>
					))}

					{user && (
						<Button 
							color="primary" size="small" variant="contained" fullWidth
							sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}
							startIcon={<LogoutIcon fontSize="small" />}
							onClick={() => {
								logout().then(() => {
									navigate('/');
									toggleDrawer(false)();
								});
							}}
						>
							Logout
						</Button>
					)}

				</Box>
			</Drawer>
		</React.Fragment>
	);
}

export default MobileToolbar;
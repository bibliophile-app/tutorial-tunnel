import React from 'react';
import { useAuth } from '../../utils/AuthContext'

import { Link } from 'react-router-dom';
import { Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import LogoutIcon from '@mui/icons-material/Logout';

import SearchBar from '../search/SearchBar';


function DesktopToolbar({ options, user }) {
	const { handleSignin, handleSignup, logout } = useAuth();
	const navigate = useNavigate();

	return (
		<React.Fragment>
			{!user && (
				<React.Fragment>

					<Button
						variant="text"
						color="neutral.main"
						size="small"
						onClick={handleSignin}
					>
						Login
					</Button>

					<Button 
						variant="text" 
						color="neutral.main" 
						size="small" 
						onClick={handleSignup}
					> 
						Criar Conta 
					</Button>

				</React.Fragment>
			)}

			{options.map((option) => (
			<Button key={option.name} variant="text" color="neutral.main" size="small" component={Link} to={option.path}>
				{option.name}
			</Button>
			))}

			<SearchBar />

			{user && (
				<Button
					color="primary"
					size="small"
					variant="contained"
					startIcon={<LogoutIcon fontSize="small" />}
					onClick={() => {
					logout().then(() => navigate('/'));
					}}
					sx={{ textTransform: 'none', fontWeight: 500 }}
				>
					Logout
				</Button>
			)}
			
  		</React.Fragment>
	);
}

export default DesktopToolbar;
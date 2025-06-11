import {Box, CircularProgress} from '@mui/material';

function LoadingBox() {
    return (
        <Box textAlign="center" mt={6}>
            <CircularProgress />
        </Box>
    )
}

export default LoadingBox;
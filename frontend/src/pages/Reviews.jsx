import React, { useEffect, useState } from 'react';
import { Box, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useParams } from 'react-router-dom';

import { searchByUser } from '../components/reviews/utils';
import { useSafeNavigate } from '../utils/useSafeNavigate';
import { useNotification } from '../utils/NotificationContext';
import ReviewCard from '../components/reviews/ReviewCard';
import Divider from '../atoms/Divider'
import LoadingBox from '../atoms/LoadingBox';

function ReviewsPage() {
    const safeBack = useSafeNavigate();
    const { username } = useParams();
    const { notify } = useNotification();
    const [ entries, setEntries ] =  useState([]);
    const [ loading, setIsLoading ] = useState(true);

    useEffect(() => {
        if (!isNaN(Number(username)) || !username) {
            notify({
                message: 'Nome de usuário inválido!',
                severity: 'error'
            });
            
            setTimeout(() => safeBack(), 1500);
            return;
        }

        setIsLoading(true);

        const fetchReviews = async () => {
            try {
                const reviews = await searchByUser(username);
                setEntries(reviews);
                setIsLoading(false);
            } catch (error) {
                notify({
                    message: 'Erro ao carregar resenhas ou usuário não encontrado!',
                    severity: 'error'
                });
                setTimeout(() => {setIsLoading(false); safeBack();}, 1500);
            }
        };

        fetchReviews();
    }, [username]);

    if (loading)
        return <LoadingBox />
    else return (
      <Box sx={{ display: 'flex', flexDirection: 'column', px: { xs: 3, lg: 0 }  }}>
        <Box sx={{display: 'flex', gap: 0.5}}>
            <Typography variant='span'>
                resenhas por
            </Typography>
            <Typography
                variant="span"
                color="neutral.main"
                fontWeight="bold"
                component={RouterLink}
                to={`${username}/profile/`}
                onClick={(e) => e.stopPropagation()}
            >
                {username}
            </Typography>
        </Box>
        
        <Divider sx={{ my: 1 }}/>

    {entries && entries.filter(e => e.content).length > 0 ? (
        entries
            .filter(e => e.content)
            .map((entry, index) => (
                <React.Fragment key={entry.id}>
                    {index !== 0 && <Divider sx={{ opacity: 0.5, my: 1 }} />}
                    <ReviewCard
                        review={entry}
                        displayDate={true}
                        displayOwner={false}
                        displayContent={true}
                        displayBookDetails={true}
                    />
                </React.Fragment>
            ))
    ) : ( 
        <Typography variant="p" sx={{ mb: 2 }}>
            Parece que ainda não há resenhas escritas por {username}...
        </Typography>
    )}
      </Box>
    );
}

export default ReviewsPage;
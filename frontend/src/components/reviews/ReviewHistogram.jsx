import { Box, Typography, Stack, Paper } from '@mui/material';
import { BarChart, Bar, XAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

import StyledRating from '../../atoms/Rating';

const ReviewHistogram = ({ reviews }) => {
  const normalizeRating = (rate) => rate / 2;
  const formatCount = (num) => {
    if (num >= 1000) {
      return (num / 1000).toFixed(num >= 10000 ? 0 : 1) + 'K';
    }
    return num;
  };

  const groupRatings = (reviews) => {
    if (!reviews || reviews.length === 0) return [];
    // Create bins for ratings from 0.0 to 5.0 in increments of 0.5
    const bins = Array.from({ length: 11 }, (_, i) => (i * 0.5).toFixed(1));
    const counts = Object.fromEntries(bins.map(bin => [bin, 0]));

    reviews.forEach(review => {
      const norm = normalizeRating(review.rate).toFixed(1);
      if (counts.hasOwnProperty(norm)) {
        counts[norm]++;
      }
    });

    return bins.map(bin => ({ rating: parseFloat(bin), count: counts[bin] }));
  };

  const data = groupRatings(reviews);

  const average = reviews && reviews.length
    ? (reviews.reduce((acc, review) => acc + normalizeRating(review.rate), 0) / reviews.length).toFixed(2)
    : 0;

  return (
    <Paper elevation={0} sx={{ bgcolor: 'transparent', width: '100%' }}>
      {reviews?.length > 4 ? (
        <Stack direction="row" spacing={2} alignItems="center">
          <Box sx={{ width: {sm: 240, md: 180}, height: 100}}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data} barCategoryGap={0}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} strokeOpacity={0.1} />
                <XAxis
                  dataKey="rating"
                  tick={{ fontSize: 12, fill: '#666' }}
                  domain={[0, 5]}
                  type="category"
                  hide
                />

                <Tooltip
                  cursor={{ fill: 'rgba(0,0,0,0.1)' }}
                  contentStyle={{
                    background: '#fff',
                    borderRadius: '4px',
                    border: '1px solid #ccc',
                    fontSize: '12px'
                  }}
                  formatter={(value) => [formatCount(value), 'Reviews']}
                  labelFormatter={(label) => `${label} stars`}
                />

                <Bar dataKey="count"
                    fill="#44515C" 
                    radius={[2, 2, 0, 0]} 
                    minPointSize={2}
                />
              </BarChart>
            </ResponsiveContainer>
          </Box>

          <Box sx={{ 
            display: 'flex', 
            flexDirection: 'column', 
            justifyContent: 'center', 
            alignItems: 'center',
            width: '20%'
          }}>
            <Typography variant="h5" fontSize="1rem" >{average}</Typography>
            <StyledRating
              name="average-rating"
              value={parseFloat(average)}
              precision={0.1}
              readOnly
              sx={{ fontSize: '0.8rem' }}
            />
          </Box>
        </Stack>
      ) : (
        <Typography variant="h5" sx={{ fontSize: '0.8rem' }}>
          Not enough ratings!
        </Typography>
      )}
    </Paper>
  );
};

export default ReviewHistogram;
import { useState } from 'react';
import { Typography, Link } from '@mui/material';

const MAX_LENGTH = 300;

const DescriptionText = ({ description }) => {
  const [expanded, setExpanded] = useState(false);

  const descText = typeof description === 'string'
    ? description
    : description?.value || '';

  const isLong = descText.length > MAX_LENGTH;
  const visibleText = expanded ? descText : descText.slice(0, MAX_LENGTH);

  const toggleExpanded = (e) => {
    e.stopPropagation();
    setExpanded((prev) => !prev);
  };

  return (
    <Typography variant="body1" sx={{ mt: 2, minHeight: '5vw' }}>
        {visibleText}
        {isLong && !expanded && '...'}
        {isLong && (
        <Link onClick={toggleExpanded} sx={{ p: 1, color: 'neutral.main', opacity: 0.5 }}>
          {expanded ? 'show less' : 'show more'}
        </Link>
      )}
    </Typography>
  );
};

export default DescriptionText;

import { useState, useRef, useEffect } from 'react';
import { styled } from '@mui/material/styles';
import InputBase from '@mui/material/InputBase';
import IconButton from '@mui/material/IconButton';
import SearchIcon from '@mui/icons-material/Search';

import { useNavigate } from 'react-router-dom';

const SearchContainer = styled('div', {
  shouldForwardProp: (prop) => prop !== 'isOpen'
})(({ theme, isOpen }) => ({
  position: 'relative',
  height: '1rem',
  display: 'flex',
  borderRadius: '20px',
  alignItems: 'center',
  flexDirection: 'row-reverse',
  color: theme.palette.neutral.main,
  backgroundColor: isOpen ? 'rgba(255, 255, 255, 0.25)' : 'transparent',
  width: isOpen ? '150px' : '40px',
  padding: isOpen ? theme.spacing(0.5, 1) : 0,
  overflow: 'hidden',

  transition: [
    theme.transitions.create(['background-color'], {
      duration: theme.transitions.duration.short,
      easing: theme.transitions.easing.easeInOut,
    }),
    theme.transitions.create(['width', 'padding'], {
      duration: theme.transitions.duration.standard,
      easing: theme.transitions.easing.easeInOut,
    }),
  ].join(','),

  '&:focus-within': {
    backgroundColor: isOpen ? theme.palette.neutral.secondary : 'transparent',
    color: isOpen ? theme.palette.background.default : theme.palette.neutral.main,
  }
}));

const StyledInputBase = styled(InputBase, {
  shouldForwardProp: (prop) => prop !== 'isOpen'
})(({ theme, isOpen }) => ({
  color: 'inherit',
  fontSize: '0.8rem',
  lineHeight: 1.2, 
  flex: 1,

  opacity: isOpen ? 1 : 0,
  pointerEvents: isOpen ? 'auto' : 'none',

  '& .MuiInputBase-input': {
    padding: theme.spacing(1),
    width: '100%',
  },
}));

const StyledIconButton = styled(IconButton)(({ theme }) => ({
  color: 'inherit',
  padding: 4,
  minWidth: 0,
  width: '24px',
  height: '24px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
}));

function SearchBar() {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const inputRef = useRef(null);
  const navigate = useNavigate();

  function onSearch() {
    if (query && query.trim()) {
      navigate(`/search/${encodeURIComponent(query.trim())}`);
      setQuery("");
    }
  };

  function handleToggle() {
    setOpen((prev) => !prev);
  }

  function handleSubmit(event) {
    if (event.key === "Enter") {
      event.preventDefault();
      onSearch();
    }
  }

  useEffect(() => {
    if (open && inputRef.current) {
      inputRef.current.focus();
    }
  }, [open]);

  return (
    <SearchContainer isOpen={open}>
      <StyledInputBase
          inputRef={inputRef}
          value={query}
          onKeyDown={(e) => handleSubmit(e)}
          onChange={(e) => setQuery(e.target.value)}
          placeholder=""
          isOpen={open}
      />
      <StyledIconButton onClick={handleToggle}>
        <SearchIcon fontSize='small' />
      </StyledIconButton>
    </SearchContainer>
  );
}

export default SearchBar;

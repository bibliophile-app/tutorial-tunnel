import { useNavigate, useNavigationType } from 'react-router-dom';

function useSafeNavigate() {
  const navigate = useNavigate();
  const navigationType = useNavigationType();

  const safeBack = (fallback = '/') => {
    if (navigationType === 'POP') {
      navigate(fallback);
    } else {
      navigate(-1);
    }
  };

  return safeBack;
}

export { useSafeNavigate };
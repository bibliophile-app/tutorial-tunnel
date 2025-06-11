import { Avatar } from '@mui/material'

function UserAvatar({ username = '' }) {
  return (
    <Avatar sx={{ bgcolor: 'background.muted' }}>
        {username.charAt(0).toUpperCase()}
      </Avatar>
  )
}

export default UserAvatar;
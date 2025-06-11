import { useState } from "react";

const API_URL = "/followers";

export default function FollowerManager() {
  const [userId, setUserId] = useState("");
  const [followers, setFollowers] = useState([]);
  const [following, setFollowing] = useState([]);
  const [followedId, setFollowedId] = useState("");
  const [isFollowingResult, setIsFollowingResult] = useState(null);
  const [message, setMessage] = useState("");

  const fetchFollowers = async () => {
    try {
      if (userId.trim() == "") return;
      const res = await fetch(`${API_URL}/${userId}/followers`, {
        credentials: "include"
      });
      const data = await res.json();
      setFollowers(data);
    } catch (err) {
      console.error("Error fetching followers:", err);
    }
  };

  const fetchFollowing = async () => {
    try {
      if (userId.trim() == "") return;
      const res = await fetch(`${API_URL}/${userId}/following`, {
        credentials: "include"
      });
      const data = await res.json();
      setFollowing(data);
    } catch (err) {
      console.error("Error fetching following:", err);
    }
  };

  const checkIsFollowing = async () => {
    try {
      const res = await fetch(`${API_URL}/check?followerId=${userId}&followeeId=${followedId}`, {
        credentials: "include"
      });      
      const data = await res.json();
      setIsFollowingResult(data.isFollowing);
    } catch (err) {
      console.error("Error checking follow status:", err);
    }
  };

  const handleAddFollow = async () => {
    try {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
          followerId: parseInt(userId),
          followeeId: parseInt(followedId),
        }),
      });

      const text = await res.text();
      setMessage(text);
      fetchFollowing(); // atualiza a lista após follow
    } catch (err) {
      console.error("Error adding follow:", err);
    }
  };

  const handleDeleteFollow = async () => {
    try {
      const res = await fetch(API_URL, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
          followerId: parseInt(userId),
          followeeId: parseInt(followedId),
        }),
      });

      const text = await res.text();
      setMessage(text);
      fetchFollowing(); // atualiza a lista após unfollow
    } catch (err) {
      console.error("Error deleting follow:", err);
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Follower Manager</h1>

      <div className="mb-4">
        <label className="block font-semibold">User ID:</label>
        <input
          type="number"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
          className="border p-2 w-48"
        />
      </div>

      <div className="flex gap-2 mb-4">
        <button onClick={fetchFollowers} className="bg-blue-500 text-white p-2 rounded">
          Get Followers
        </button>
        <button onClick={fetchFollowing} className="bg-green-500 text-white p-2 rounded">
          Get Following
        </button>
      </div>

      <div className="mb-4">
        <label className="block font-semibold">Followed User ID:</label>
        <input
          type="number"
          value={followedId}
          onChange={(e) => setFollowedId(e.target.value)}
          className="border p-2 w-48"
        />
        <div className="flex gap-2 mt-2">
          <button onClick={checkIsFollowing} className="bg-purple-500 text-white p-2 rounded">
            Check Follow
          </button>
          <button onClick={handleAddFollow} className="bg-blue-700 text-white p-2 rounded">
            Follow
          </button>
          <button onClick={handleDeleteFollow} className="bg-red-500 text-white p-2 rounded">
            Unfollow
          </button>
        </div>
      </div>

      {isFollowingResult !== null && (
        <p className="mt-2">
          Follow status: <strong>{isFollowingResult ? "Following" : "Not following"}</strong>
        </p>
      )}

      {message && <p className="mt-2 text-green-600">{message}</p>}

      <div className="mt-6">
        <h2 className="text-xl font-semibold">Followers</h2>
        <ul className="list-disc pl-6">
          {followers.map((f) => (
            <li key={f.id}>User {f.following_user_id} follows you</li>
          ))}
        </ul>
      </div>

      <div className="mt-4">
        <h2 className="text-xl font-semibold">You Are Following</h2>
        <ul className="list-disc pl-6">
          {following.map((f) => (
            <li key={f.id}>You follow user {f.followed_user_id}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}
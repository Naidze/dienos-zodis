import React, { useState, useEffect } from "react";
import { wordApi } from "./services/api";
import "./App.sass";
import { Word } from "./types/word";
import Card from "./components/Card";

const App: React.FC = () => {
  const [todayWord, setTodayWord] = useState<Word | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadTodayWord();
  }, []);

  const loadTodayWord = async (): Promise<void> => {
    try {
      setLoading(true);
      setError(null);
      const response = await wordApi.getTodayWord();
      setTodayWord(response.data);
    } catch (err: any) {
      console.error("Error loading today's word:", err);
      setError(err.response?.data?.message || "Failed to load word");
    } finally {
      setLoading(false);
    }
  };

  const handleClaimWord = async (): Promise<void> => {
    if (!todayWord) return;

    try {
      // TODO: Get actual user ID from authentication
      const userId = 1;
      await wordApi.claimWord(todayWord.id, userId);
      alert("Word claimed successfully!");
    } catch (err: any) {
      console.error("Error claiming word:", err);
      alert(err.response?.data?.message || "Failed to claim word");
    }
  };

  if (loading) {
    return (
      <div className="App">
        <div className="loading">Loading...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="App">
        <div className="error">
          <h2>Error</h2>
          <p>{error}</p>
          <button onClick={loadTodayWord}>Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>🇱🇹 Lithuanian Word of the Day</h1>
      </header>

      <main className="App-main">
        {todayWord ? (
          <>
            <div className="flip-card-container">
              <Card word={todayWord} />
            </div>

            <div className="word-actions">
              <button className="claim-button" onClick={handleClaimWord}>
                📚 Claim This Card
              </button>
            </div>

            <div className="word-meta">
              <small>
                Date: {new Date(todayWord.wordDate).toLocaleDateString()}
              </small>
            </div>
          </>
        ) : (
          <div className="no-word">
            <p>No word available for today</p>
            <button onClick={loadTodayWord}>Refresh</button>
          </div>
        )}
      </main>
    </div>
  );
};

export default App;

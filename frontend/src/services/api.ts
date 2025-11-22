import axios, { AxiosResponse, AxiosError } from 'axios';
import { Word } from '../types/word';
import { ClaimedCard } from '../types/claimedCard';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Retry configuration for handling server wake-up delays
const MAX_RETRIES = 10;
const INITIAL_DELAY_MS = 1000; // 1 second
const MAX_DELAY_MS = 30000; // 30 seconds

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const wordApi = {
  getTodayWord: (): Promise<AxiosResponse<Word>> => {
    return apiClient.get<Word>('/words/today');
  },

  getWordHistory: (): Promise<AxiosResponse<Word[]>> => {
    return apiClient.get<Word[]>('/words/history');
  },

  claimWord: (wordId: number, userId: number): Promise<AxiosResponse<ClaimedCard>> => {
    return apiClient.post<ClaimedCard>(`/words/${wordId}/claim`, null, {
      params: { userId },
    });
  },

  getUserClaimedCards: (userId: number): Promise<AxiosResponse<ClaimedCard[]>> => {
    return apiClient.get<ClaimedCard[]>(`/words/claimed/${userId}`);
  },

  generateWord: (): Promise<AxiosResponse<string>> => {
    return apiClient.post<string>('/words/generate');
  },
};

// Exponential backoff retry interceptor for handling server wake-up
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const config = error.config as any;

    // Initialize retry count if not already set
    if (!config.retryCount) {
      config.retryCount = 0;
    }

    // Only retry on network errors or 5xx server errors (not 4xx client errors)
    const shouldRetry =
      (!error.response || error.response.status >= 500) &&
      config.retryCount < MAX_RETRIES;

    if (shouldRetry) {
      config.retryCount += 1;

      // Exponential backoff with jitter
      const delayMs = Math.min(
        INITIAL_DELAY_MS * Math.pow(2, config.retryCount - 1) + Math.random() * 1000,
        MAX_DELAY_MS
      );

      console.warn(
        `Retry attempt ${config.retryCount}/${MAX_RETRIES} after ${delayMs}ms. ` +
          `Server may be waking up...`
      );

      await new Promise((resolve) => setTimeout(resolve, delayMs));
      return apiClient.request(config);
    }

    // Log final error
    if (error.response) {
      console.error('API Error:', error.response.data);
    } else if (error.request) {
      console.error('Network Error:', error.message);
    } else {
      console.error('Error:', error.message);
    }

    return Promise.reject(error);
  }
);

export default apiClient;
import axios, { AxiosResponse } from 'axios';
import { Word } from '../types/word';
import { ClaimedCard } from '../types/claimedCard';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

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

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
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
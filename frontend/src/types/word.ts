export interface Word {
  id: number;
  word: string;
  definitionLt: string;
  definitionEn: string;
  usageExampleLt?: string;
  usageExampleEn?: string;
  imageUrl?: string;
  wordDate: string; // ISO date format
  createdAt: string;
}
import { User } from "./user";
import { Word } from "./word";

export interface ClaimedCard {
  id: number;
  user: User;
  word: Word;
  claimedAt: string;
}
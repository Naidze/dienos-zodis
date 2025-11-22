import { useState } from "react";
import CardImage from "./CardImage";
import "./Card.sass";

export default function Card({ word }) {
  const [isFlipped, setIsFlipped] = useState(false);

  const handleFlip = () => {
    setIsFlipped(!isFlipped);
  };

  return (
    <div
      className={`flip-card ${isFlipped ? "flipped" : ""}`}
      onClick={handleFlip}
    >
      {/* Front Side - Lithuanian */}
      <div className="flip-card-front">
        {word.imageUrl && (
          <CardImage imageUrl={word.imageUrl} word={word.word} />
        )}

        <h2 className="word-title">{word.word}</h2>

        <div className="definition-section">
          <div className="section-label">Apibrėžimas</div>
          <p className="definition-text">{word.definitionLt}</p>
        </div>

        {word.usageExampleLt && (
          <div className="example-section lt">
            <div className="section-label">Pavyzdys</div>
            <p className="example-text">"{word.usageExampleLt}"</p>
          </div>
        )}

        <div className="flip-hint">🔄 Click to see English translation</div>
      </div>

      {/* Back Side - English */}
      <div className="flip-card-back">
        <CardImage imageUrl={word.imageUrl} word={word.word} />

        <h2 className="word-title">{word.word}</h2>

        <div className="definition-section">
          <div className="section-label">Definition</div>
          <p className="definition-text">{word.definitionEn}</p>
        </div>

        {word.usageExampleEn && (
          <div className="example-section en">
            <div className="section-label">Example</div>
            <p className="example-text">"{word.usageExampleEn}"</p>
          </div>
        )}

        <div className="flip-hint">🔄 Click to see Lithuanian</div>
      </div>
    </div>
  );
}

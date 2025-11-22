import "./CardImage.sass";

export default function CardImage({
  imageUrl,
  word,
}: {
  imageUrl?: string;
  word: string;
}): JSX.Element | null {
  console.log(imageUrl);
  if (!imageUrl) {
    return null;
  }

  return (
    <div className="card-image">
      <img
        src={imageUrl}
        alt={word}
        style={{
          maxWidth: "100%",
          borderRadius: "10px",
          boxShadow: "0 4px 12px rgba(0, 0, 0, 0.1)",
        }}
      />
    </div>
  );
}

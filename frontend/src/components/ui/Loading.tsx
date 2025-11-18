import React from 'react';
import { Loader2 } from 'lucide-react';

interface LoadingProps {
  size?: 'sm' | 'md' | 'lg';
  fullScreen?: boolean;
  text?: string;
}

const Loading: React.FC<LoadingProps> = ({ size = 'md', fullScreen = false, text }) => {
  const sizeMap = {
    sm: 24,
    md: 40,
    lg: 56,
  };

  const content = (
    <div className="flex flex-col items-center justify-center gap-3">
      <Loader2 className="animate-spin text-primary-500" size={sizeMap[size]} />
      {text && <p className="text-gray-600">{text}</p>}
    </div>
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-white bg-opacity-90 z-50">
        {content}
      </div>
    );
  }

  return content;
};

export default Loading;

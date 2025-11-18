import React, { InputHTMLAttributes, forwardRef } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  fullWidth?: boolean;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, helperText, fullWidth = false, className = '', ...props }, ref) => {
    const widthStyle = fullWidth ? 'w-full' : '';

    return (
      <div className={widthStyle}>
        {label && (
          <label className="block text-sm font-medium text-dark-text-primary mb-1.5">
            {label}
          </label>
        )}
        <input
          ref={ref}
          className={`
            px-4 py-2.5 border rounded-md text-base
            bg-dark-bg-card text-dark-text-primary
            transition-all duration-200
            focus:outline-none focus:border-accent-blue
            disabled:opacity-50 disabled:cursor-not-allowed
            placeholder:text-dark-text-muted
            ${error ? 'border-accent-red' : 'border-dark-border'}
            ${fullWidth ? 'w-full' : ''}
            ${className}
          `}
          {...props}
        />
        {error && (
          <p className="mt-1.5 text-sm text-accent-red">{error}</p>
        )}
        {helperText && !error && (
          <p className="mt-1.5 text-sm text-dark-text-secondary">{helperText}</p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';

export default Input;

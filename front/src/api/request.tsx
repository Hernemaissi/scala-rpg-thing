import { useAuth0 } from '@auth0/auth0-react';
import { useCallback } from 'react';
import makeRequest from 'axios';

export const useRequest = () => {
  const { getAccessTokenSilently } = useAuth0();

  // memoized the function, as otherwise if the hook is used inside a useEffect, it will lead to an infinite loop
  const memoizedFn = useCallback(
    async (request) => {
      const accessToken = await getAccessTokenSilently({ audience: "https://adventure-test.example.com" })
      return makeRequest({
        ...request,
        headers: {
          ...request.headers,
          // Add the Authorization header to the existing headers
          Authorization: `Bearer ${accessToken}`,
        },
      }).then(r => r.data);
    },
    [getAccessTokenSilently]
  );
  return {
    requestMaker: memoizedFn,
  };
};

export default useRequest;